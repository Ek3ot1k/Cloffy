package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Location;
import ru.amin.Rest.entity.Users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location,Integer> {

    // Текущая геолокация конкретного пользователя (одна запись на пользователя)
    Optional<Location> findByUser(Users user);

    // Геолокации списка пользователей (для получения позиций друзей)
    List<Location> findByUserIn(List<Users> users);

    // Только свежие геолокации, чтобы не уведомлять о пользователях,
    // которые были рядом давно и уже могли уехать.
    List<Location> findByTimestampAfter(LocalDateTime timestamp);
}
