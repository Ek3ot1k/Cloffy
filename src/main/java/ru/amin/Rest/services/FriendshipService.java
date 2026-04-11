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

    public void addFriendRequest(Users user,Users friend){
        Friendship friendship=new Friendship(user,friend);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
    }

    public void acceptFriendRequest(Users user,Users friend){
        Friendship friendship=friendshipRepository.getFriendshipByFriendBetween(user,friend);
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
    }

    public void deleteFriendship(Users user,Users friend){
        Friendship friendship=friendshipRepository.getFriendshipByFriendBetween(user,friend);
        friendshipRepository.delete(friendship);
    }
}
