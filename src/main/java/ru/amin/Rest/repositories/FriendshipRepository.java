package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Friendship;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.util.FriendshipStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship,Integer> {

    // Найти дружбу между двумя пользователями в любом направлении
    @Query("SELECT f FROM Friendship f WHERE (f.user = :user AND f.friend = :friend) OR (f.user = :friend AND f.friend = :user)")
    Optional<Friendship> findFriendshipBetween(@Param("user") Users user, @Param("friend") Users friend);

    // Все дружбы пользователя (где он инициатор или получатель)
    @Query("SELECT f FROM Friendship f WHERE f.user = :user OR f.friend = :user")
    List<Friendship> findAllByUser(@Param("user") Users user);

    // Принятые дружбы пользователя
    @Query("SELECT f FROM Friendship f WHERE (f.user = :user OR f.friend = :user) AND f.status = :status")
    List<Friendship> findAllByUserAndStatus(@Param("user") Users user, @Param("status") FriendshipStatus status);
}
