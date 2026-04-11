package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Friendship;
import ru.amin.Rest.entity.Users;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship,Integer> {
    Friendship getFriendshipByFriendBetween(Users user,Users friend);
}
