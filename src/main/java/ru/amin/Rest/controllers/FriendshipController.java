package ru.amin.Rest.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.entity.Friendship;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.FriendshipRepository;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.services.FriendshipService;
import ru.amin.Rest.services.UserService;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/allFriends")
    public List<Friendship> getFriends(){
        return friendshipRepository.findAll();
    }

    @PostMapping("/sendRequest/{id}")
    public ResponseEntity<HttpStatus> sendRequest(@PathVariable("id") int id){
        Users user=userRepository.findById(id).orElseThrow(()->new UserNotFoundException("Такого пользователя не существует"));
        friendshipService.addFriendRequest(user);

        return ResponseEntity.ok(HttpStatus.OK);
    }


}
