package ru.amin.Rest.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.amin.Rest.entity.Friendship;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.BlockRepository;
import ru.amin.Rest.repositories.FriendshipRepository;
import ru.amin.Rest.util.FriendshipStatus;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final BlockRepository blockRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, BlockRepository blockRepository) {
        this.friendshipRepository = friendshipRepository;
        this.blockRepository = blockRepository;
    }

    public void addFriendRequest(Users user, Users friend) {
        // Нельзя добавить в друзья заблокированного пользователя
        if (blockRepository.isBlockedInAnyDirection(user, friend)) {
            throw new UserNotFoundException("Невозможно отправить запрос этому пользователю");
        }
        Friendship friendship = new Friendship(user, friend);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
    }

    @Transactional
    public void acceptFriendRequest(Users user, Users friend) {
        Friendship friendship = friendshipRepository.findFriendshipBetween(user, friend)
                .orElseThrow(() -> new UserNotFoundException("Запрос в друзья не найден"));
        friendship.setStatus(FriendshipStatus.ACCEPTED);
    }

    public void deleteFriendship(Users user, Users friend) {
        Friendship friendship = friendshipRepository.findFriendshipBetween(user, friend)
                .orElseThrow(() -> new UserNotFoundException("Дружба не найдена"));
        friendshipRepository.delete(friendship);
    }

    // Все дружбы пользователя (входящие и исходящие)
    public List<Friendship> getAllFriendships(Users user) {
        return friendshipRepository.findAllByUser(user);
    }

    // Только принятые друзья пользователя в виде списка Users
    public List<Users> getAcceptedFriends(Users user) {
        return friendshipRepository.findAllByUserAndStatus(user, FriendshipStatus.ACCEPTED)
                .stream()
                .map(f -> f.getUser().getId() == user.getId() ? f.getFriend() : f.getUser())
                .toList();
    }
}
