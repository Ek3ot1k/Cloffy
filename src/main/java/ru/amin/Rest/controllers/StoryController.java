package ru.amin.Rest.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.dto.StoryCreateDTO;
import ru.amin.Rest.dto.StoryResponseDTO;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.StoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    // Опубликовать историю (исчезнет через 24 часа)
    @PostMapping
    public ResponseEntity<StoryResponseDTO> createStory(
            @RequestBody @Valid StoryCreateDTO dto,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        StoryResponseDTO story = storyService.createStory(usersDetails.getUser(), dto);
        return ResponseEntity.ok(story);
    }

    // Активные истории всех друзей
    @GetMapping("/friends")
    public ResponseEntity<List<StoryResponseDTO>> getFriendsStories(
            @AuthenticationPrincipal UsersDetails usersDetails) {
        List<StoryResponseDTO> stories = storyService.getFriendsStories(usersDetails.getUser());
        return ResponseEntity.ok(stories);
    }
}
