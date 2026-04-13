package ru.amin.Rest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.amin.Rest.dto.ChatMessageDTO;
import ru.amin.Rest.dto.LocationDTO;
import ru.amin.Rest.dto.MessageResponseDTO;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.services.FriendshipService;
import ru.amin.Rest.services.LocationService;
import ru.amin.Rest.services.MessageService;
import ru.amin.Rest.services.ProximityService;
import ru.amin.Rest.util.UserNotFoundException;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final FriendshipService friendshipService;
    private final LocationService locationService;
    private final MessageService messageService;
    private final ProximityService proximityService;
    private final UserRepository userRepository;

    // Геолокация: сохраняем в БД, рассылаем друзьям, проверяем близость
    // Используем Principal вместо @AuthenticationPrincipal — в WebSocket-контексте
    // @AuthenticationPrincipal не разрешается корректно (user = null внутри UsersDetails)
    @MessageMapping("/location")
    public void handleLocation(LocationDTO locationDTO, Principal principal) {
        if (principal == null) return;

        Users currentUser = userRepository.findByName(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + principal.getName()));

        locationService.saveOrUpdateLocation(currentUser, locationDTO);

        // Рассылаем геолокацию принятым друзьям
        List<Users> friends = friendshipService.getAcceptedFriends(currentUser);
        for (Users friend : friends) {
            messagingTemplate.convertAndSendToUser(friend.getName(), "/queue/locations", locationDTO);
        }

        // Проверяем близость с незнакомыми пользователями
        List<ProximityService.ProximityResult> nearby = proximityService.findNearbyStrangers(
                currentUser, locationDTO.getLat(), locationDTO.getLng()
        );
        for (ProximityService.ProximityResult result : nearby) {
            // Уведомляем незнакомца о текущем пользователе
            messagingTemplate.convertAndSendToUser(
                    result.otherUser().getName(), "/queue/nearby", result.notificationForOther()
            );
            // Уведомляем текущего пользователя о незнакомце
            messagingTemplate.convertAndSendToUser(
                    currentUser.getName(), "/queue/nearby", result.notificationForCurrent()
            );
        }
    }

    // Чат: сохраняем сообщение и доставляем получателю в реальном времени
    @MessageMapping("/chat")
    public void handleChat(ChatMessageDTO chatMessageDTO, Principal principal) {
        if (principal == null) return;

        Users sender = userRepository.findByName(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + principal.getName()));
        Users receiver = userRepository.findById(chatMessageDTO.getReceiverId())
                .orElseThrow(() -> new UserNotFoundException("Получатель не найден"));

        MessageResponseDTO response = messageService.sendMessage(sender, receiver, chatMessageDTO.getContent());

        // Доставляем сообщение получателю
        messagingTemplate.convertAndSendToUser(receiver.getName(), "/queue/messages", response);
        // Отправляем эхо отправителю (для подтверждения доставки)
        messagingTemplate.convertAndSendToUser(sender.getName(), "/queue/messages", response);
    }
}
