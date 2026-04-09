package ru.amin.Rest.dto;

import lombok.Getter;
import lombok.Setter;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.util.FriendshipStatus;

@Getter
@Setter
public class FriendshipDTO {
    private Users user;

    private Users friend;

    private FriendshipStatus status;
}
