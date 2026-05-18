package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByName(String name);

    // Поиск пользователей по нику (частичное совпадение, регистронезависимый)
    List<Users> findByNameContainingIgnoreCase(String name);

    // Поиск по школе (регистронезависимый)
    List<Users> findBySchoolIgnoreCase(String school);

    // Поиск по университету (регистронезависимый)
    List<Users> findByUniversityIgnoreCase(String university);
}
