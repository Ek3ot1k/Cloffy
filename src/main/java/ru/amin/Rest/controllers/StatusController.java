package ru.amin.Rest.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.dto.UserStatusDTO;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {

    private final UserRepository userRepository;

    public StatusController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Обновить свой статус (HOME, WORK или любой кастомный текст)
    @PutMapping
    public ResponseEntity<Map<String, String>> updateStatus(
            @RequestBody @Valid UserStatusDTO dto,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        Users user = userRepository.findById(usersDetails.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        user.setStatus(dto.getStatus());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("status", user.getStatus()));
    }

    // Получить статус любого пользователя по id
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable int userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        String status = user.getStatus() != null ? user.getStatus() : "";
        return ResponseEntity.ok(Map.of("userId", String.valueOf(userId), "status", status));
    }
}
