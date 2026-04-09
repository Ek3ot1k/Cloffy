package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {
}
