package ru.amin.Rest.dto;

import ru.amin.Rest.entity.Users;
import ru.amin.Rest.util.FriendshipStatus;


public class FriendshipDTO {
    private Users user;

    private FriendshipStatus status;

    public FriendshipDTO() {
    }

    public FriendshipDTO(Users user, FriendshipStatus status) {
        this.user = user;
        this.status = status;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
}
