package ru.amin.Rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.entity.Frame;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.ShopService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    // Все рамки в магазине с ценами
    @GetMapping("/frames")
    public ResponseEntity<List<Frame>> getAllFrames() {
        return ResponseEntity.ok(shopService.getAllFrames());
    }

    // Купленные рамки текущего пользователя
    @GetMapping("/frames/my")
    public ResponseEntity<List<Frame>> getMyFrames(
            @AuthenticationPrincipal UsersDetails usersDetails) {
        return ResponseEntity.ok(shopService.getMyFrames(usersDetails.getUser()));
    }

    // Купить рамку — спишет монеты и добавит в коллекцию
    @PostMapping("/frames/{frameId}/buy")
    public ResponseEntity<Map<String, Object>> buyFrame(
            @PathVariable int frameId,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        int remaining = shopService.buyFrame(usersDetails.getUser(), frameId);
        return ResponseEntity.ok(Map.of(
                "result", "bought",
                "coinsRemaining", remaining
        ));
    }

    // Надеть рамку на аватар
    @PostMapping("/frames/{frameId}/equip")
    public ResponseEntity<Map<String, String>> equipFrame(
            @PathVariable int frameId,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        shopService.equipFrame(usersDetails.getUser(), frameId);
        return ResponseEntity.ok(Map.of("result", "equipped"));
    }

    // Снять рамку
    @DeleteMapping("/frames/equip")
    public ResponseEntity<Map<String, String>> unequipFrame(
            @AuthenticationPrincipal UsersDetails usersDetails) {
        shopService.unequipFrame(usersDetails.getUser());
        return ResponseEntity.ok(Map.of("result", "unequipped"));
    }
}
