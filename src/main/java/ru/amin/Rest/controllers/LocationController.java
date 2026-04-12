package ru.amin.Rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.amin.Rest.dto.FriendLocationDTO;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.LocationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // Возвращает последние геолокации всех принятых друзей текущего пользователя
    @GetMapping("/friends")
    public ResponseEntity<List<FriendLocationDTO>> getFriendsLocations(
            @AuthenticationPrincipal UsersDetails usersDetails) {
        List<FriendLocationDTO> locations = locationService.getFriendsLocations(usersDetails.getUser());
        return ResponseEntity.ok(locations);
    }
}
