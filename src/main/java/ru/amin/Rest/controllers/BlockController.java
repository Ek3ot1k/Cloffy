package ru.amin.Rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.BlockService;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/block")
public class BlockController {

    private final BlockService blockService;
    private final UserRepository userRepository;

    public BlockController(BlockService blockService, UserRepository userRepository) {
        this.blockService = blockService;
        this.userRepository = userRepository;
    }

    // Заблокировать пользователя
    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, String>> blockUser(
            @PathVariable int userId,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        Users blocked = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        blockService.blockUser(usersDetails.getUser(), blocked);
        return ResponseEntity.ok(Map.of("result", "blocked"));
    }

    // Разблокировать пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> unblockUser(
            @PathVariable int userId,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        Users blocked = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        blockService.unblockUser(usersDetails.getUser(), blocked);
        return ResponseEntity.ok(Map.of("result", "unblocked"));
    }

    // Список заблокированных пользователей
    @GetMapping
    public ResponseEntity<List<Users>> getBlockedUsers(
            @AuthenticationPrincipal UsersDetails usersDetails) {
        List<Users> blocked = blockService.getBlockedUsers(usersDetails.getUser());
        return ResponseEntity.ok(blocked);
    }
}
