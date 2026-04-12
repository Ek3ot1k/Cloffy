package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Story;
import ru.amin.Rest.entity.Users;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Integer> {

    // Активные истории списка пользователей (не истёкшие)
    @Query("SELECT s FROM Story s WHERE s.user IN :users AND s.expiresAt > :now ORDER BY s.createdAt DESC")
    List<Story> findActiveByUsers(@Param("users") List<Users> users, @Param("now") LocalDateTime now);
}
