package ru.amin.Rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.amin.Rest.dto.WalletDTO;
import ru.amin.Rest.security.UsersDetails;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    // Баланс монет текущего пользователя
    @GetMapping
    public ResponseEntity<WalletDTO> getBalance(
            @AuthenticationPrincipal UsersDetails usersDetails) {
        return ResponseEntity.ok(new WalletDTO(usersDetails.getUser().getCoins()));
    }
}
