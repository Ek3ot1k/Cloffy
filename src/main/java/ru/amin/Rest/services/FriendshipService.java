package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import ru.amin.Rest.entity.Friendship;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.FriendshipRepository;
import ru.amin.Rest.util.FriendshipStatus;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    public FriendshipService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public void addFriendRequest(Users user1){
        Friendship friendship=new Friendship();
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
    }
}
