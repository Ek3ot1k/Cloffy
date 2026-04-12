package ru.amin.Rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.entity.Friendship;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.FriendshipRepository;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.FriendshipService;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendshipController {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipRepository friendshipRepository, UserRepository userRepository, FriendshipService friendshipService) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.friendshipService = friendshipService;
    }

    // Возвращает только дружбы текущего пользователя
    @GetMapping("/allFriends")
    public List<Friendship> getFriends(@AuthenticationPrincipal UsersDetails usersDetails) {
        return friendshipService.getAllFriendships(usersDetails.getUser());
    }

    @PostMapping("/sendRequest/{id}")
    public ResponseEntity<HttpStatus> sendRequest(@PathVariable("id") int id,
                                                  @AuthenticationPrincipal UsersDetails usersDetails){
        Users currentUser=usersDetails.getUser();
        Users friend=userRepository.findById(id).orElseThrow(()->new UserNotFoundException("Такого пользователя не существует"));
        friendshipService.addFriendRequest(currentUser,friend);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/acceptRequest/{id}")
    public ResponseEntity<HttpStatus> acceptRequest(@PathVariable("id") int id,
                                                    @AuthenticationPrincipal UsersDetails usersDetails){
        Users currentUser=usersDetails.getUser();
        Users friend=userRepository.findById(id).orElseThrow(()->new UserNotFoundException("Такого пользователя не существует"));
        friendshipService.acceptFriendRequest(currentUser,friend);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/deleteFriend/{id}")
    public ResponseEntity<HttpStatus> deleteFriend(@PathVariable("id") int id,
                                                   @AuthenticationPrincipal UsersDetails usersDetails){
        Users currentUser=usersDetails.getUser();
        Users friend=userRepository.findById(id).orElseThrow(()->new UserNotFoundException("Такого пользователя не существует"));
        friendshipService.deleteFriendship(currentUser,friend);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
