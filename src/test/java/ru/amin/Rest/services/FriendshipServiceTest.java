package ru.amin.Rest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.amin.Rest.entity.Friendship;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.BlockRepository;
import ru.amin.Rest.repositories.FriendshipRepository;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.util.FriendshipStatus;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {
    @Mock
    FriendshipRepository friendshipRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BlockRepository blockRepository;

    @InjectMocks
    FriendshipService friendshipService;

    @Test
    void addFriendRequestSavesWithPendingStatus(){
        Users user=new Users();
        user.setId(1);
        Users friend=new Users();
        friend.setId(2);

        when(friendshipRepository.findFriendshipBetween(user,friend)).thenReturn(Optional.empty());
        when(blockRepository.isBlockedInAnyDirection(user,friend)).thenReturn(false);
        friendshipService.addFriendRequest(user,friend);
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void addFriendRequestRejectsSelfRequest(){
        Users user=new Users();
        user.setId(1);

        assertThrows(UserNotFoundException.class,()->friendshipService.addFriendRequest(user,user));
        verify(friendshipRepository,never()).save(any(Friendship.class));
    }

    @Test
    void addFriendRequestRejectsBlockedUser(){
        Users user=new Users();
        user.setId(1);

        Users friend=new Users();
        friend.setId(2);

        when(blockRepository.isBlockedInAnyDirection(user,friend)).thenReturn(true);
        assertThrows(UserNotFoundException.class,()->friendshipService.addFriendRequest(user,friend));
        verify(friendshipRepository,never()).save(any(Friendship.class));
    }

    @Test
    void addFriendRequestRejectsDuplicateRequest(){
        Users user=new Users();
        user.setId(1);
        Users friend=new Users();
        friend.setId(2);

        when(friendshipRepository.findFriendshipBetween(user,friend)).thenReturn(Optional.of(new Friendship()));
        when(blockRepository.isBlockedInAnyDirection(user, friend)).thenReturn(false);
        assertThrows(UserNotFoundException.class,()->friendshipService.addFriendRequest(user,friend));
        verify(friendshipRepository,never()).save(any(Friendship.class));
    }

}
