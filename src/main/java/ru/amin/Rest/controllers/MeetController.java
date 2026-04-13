package ru.amin.Rest.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.dto.MeetRequestCreateDTO;
import ru.amin.Rest.dto.MeetResponseDTO;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.MeetService;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/meets")
public class MeetController {

    private final MeetService meetService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MeetController(MeetService meetService,
                          UserRepository userRepository,
                          SimpMessagingTemplate messagingTemplate) {
        this.meetService = meetService;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Предложить другу точку встречи. Маршрут до неё строит Swift-клиент.
    @PostMapping("/{receiverId}")
    public ResponseEntity<MeetResponseDTO> requestMeet(@PathVariable int receiverId,
                                                       @RequestBody @Valid MeetRequestCreateDTO dto,
                                                       @AuthenticationPrincipal UsersDetails usersDetails) {
        Users receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        MeetResponseDTO response = meetService.requestMeet(usersDetails.getUser(), receiver, dto);

        // Уведомляем получателя через WebSocket — он увидит запрос в реальном времени
        messagingTemplate.convertAndSendToUser(response.getReceiverName(), "/queue/meet-requests", response);

        return ResponseEntity.ok(response);
    }

    // Принять входящий запрос встречи.
    @PostMapping("/{meetId}/accept")
    public ResponseEntity<MeetResponseDTO> acceptMeet(@PathVariable int meetId,
                                                      @AuthenticationPrincipal UsersDetails usersDetails) {
        MeetResponseDTO response = meetService.acceptMeet(meetId, usersDetails.getUser());

        // Уведомляем обоих — каждый получает meetLat/meetLng для построения маршрута
        messagingTemplate.convertAndSendToUser(response.getRequesterName(), "/queue/meet-updates", response);
        messagingTemplate.convertAndSendToUser(response.getReceiverName(), "/queue/meet-updates", response);

        return ResponseEntity.ok(response);
    }

    // Отклонить входящий запрос встречи.
    @PostMapping("/{meetId}/decline")
    public ResponseEntity<MeetResponseDTO> declineMeet(@PathVariable int meetId,
                                                       @AuthenticationPrincipal UsersDetails usersDetails) {
        MeetResponseDTO response = meetService.declineMeet(meetId, usersDetails.getUser());

        // Уведомляем инициатора об отказе
        messagingTemplate.convertAndSendToUser(response.getRequesterName(), "/queue/meet-updates", response);

        return ResponseEntity.ok(response);
    }

    // Активные встречи пользователя: pending и accepted.
    @GetMapping
    public ResponseEntity<List<MeetResponseDTO>> getActiveMeets(
            @AuthenticationPrincipal UsersDetails usersDetails) {
        return ResponseEntity.ok(meetService.getActiveMeets(usersDetails.getUser()));
    }

    // Краткая карта API этого раздела.
    @GetMapping("/documentation")
    public ResponseEntity<Map<String, String>> documentation() {
        return ResponseEntity.ok(Map.of(
                "create", "POST /api/v1/meets/{receiverId}",
                "accept", "POST /api/v1/meets/{meetId}/accept",
                "decline", "POST /api/v1/meets/{meetId}/decline",
                "list", "GET /api/v1/meets"
        ));
    }
}
