package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.amin.Rest.dto.StoryCreateDTO;
import ru.amin.Rest.dto.StoryResponseDTO;
import ru.amin.Rest.entity.Story;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.StoryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final FriendshipService friendshipService;

    public StoryService(StoryRepository storyRepository, FriendshipService friendshipService) {
        this.storyRepository = storyRepository;
        this.friendshipService = friendshipService;
    }

    // Создать историю (активна 24 часа)
    @Transactional
    public StoryResponseDTO createStory(Users user, StoryCreateDTO dto) {
        Story story = new Story();
        story.setUser(user);
        story.setImageUrl(dto.getImageUrl());
        story.setCaption(dto.getCaption());
        story.setCreatedAt(LocalDateTime.now());
        story.setExpiresAt(LocalDateTime.now().plusHours(24));
        storyRepository.save(story);

        return toDTO(story);
    }

    // Активные истории всех принятых друзей
    public List<StoryResponseDTO> getFriendsStories(Users currentUser) {
        List<Users> friends = friendshipService.getAcceptedFriends(currentUser);
        if (friends.isEmpty()) return List.of();

        return storyRepository.findActiveByUsers(friends, LocalDateTime.now())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private StoryResponseDTO toDTO(Story story) {
        return new StoryResponseDTO(
                story.getId(),
                story.getUser().getId(),
                story.getUser().getName(),
                story.getImageUrl(),
                story.getCaption(),
                story.getCreatedAt(),
                story.getExpiresAt()
        );
    }
}
