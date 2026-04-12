package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Message;
import ru.amin.Rest.entity.Users;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    // Переписка между двумя пользователями в хронологическом порядке
    @Query("SELECT m FROM Message m WHERE (m.sender = :user AND m.receiver = :other) OR (m.sender = :other AND m.receiver = :user) ORDER BY m.timestamp ASC")
    List<Message> findConversation(@Param("user") Users user, @Param("other") Users other);
}
