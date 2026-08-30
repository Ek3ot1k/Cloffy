package ru.amin.Rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.dto.ConversationSummaryDTO;
import ru.amin.Rest.dto.MessageDTO;
import ru.amin.Rest.dto.MessageResponseDTO;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.BlockService;
import ru.amin.Rest.services.FriendshipService;
import ru.amin.Rest.services.MessageService;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final FriendshipService friendshipService;
    private final BlockService blockService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService,
                             UserRepository userRepository,
                             FriendshipService friendshipService,
                             BlockService blockService,
                             SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.friendshipService = friendshipService;
        this.blockService = blockService;
        this.messagingTemplate = messagingTemplate;
    }

    // Отправить сообщение (только друзьям, не заблокированным)
    @PostMapping("/{receiverId}")
    public ResponseEntity<?> sendMessage(@PathVariable int receiverId,
                                         @RequestBody MessageDTO messageDTO,
                                         @AuthenticationPrincipal UsersDetails usersDetails) {
        Users sender = usersDetails.getUser();
        Users receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Получатель не найден"));

        if (blockService.isBlocked(sender, receiver)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Невозможно отправить сообщение этому пользователю"));
        }

        boolean areFriends = friendshipService.getAcceptedFriends(sender)
                .stream()
                .anyMatch(f -> f.getId() == receiverId);

        if (!areFriends) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Сообщения можно отправлять только друзьям"));
        }

        MessageResponseDTO response = messageService.sendMessage(sender, receiver, messageDTO.getContent());
        messagingTemplate.convertAndSendToUser(receiver.getName(), "/queue/messages", response);
        messagingTemplate.convertAndSendToUser(sender.getName(), "/queue/messages", response);
        return ResponseEntity.ok(response);
    }

    // Список всех диалогов текущего пользователя (последнее сообщение каждого)
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationSummaryDTO>> getConversations(
            @AuthenticationPrincipal UsersDetails usersDetails) {
        Users currentUser = usersDetails.getUser();
        return ResponseEntity.ok(messageService.getConversations(currentUser));
    }

    // Получить историю переписки с пользователем
    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageResponseDTO>> getConversation(
            @PathVariable int userId,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        Users currentUser = usersDetails.getUser();
        Users other = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        List<MessageResponseDTO> conversation = messageService.getConversation(currentUser, other);
        return ResponseEntity.ok(conversation);
    }
}
