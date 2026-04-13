package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Frame;
import ru.amin.Rest.entity.UserFrame;
import ru.amin.Rest.entity.Users;

import java.util.List;

@Repository
public interface UserFrameRepository extends JpaRepository<UserFrame, Integer> {

    List<UserFrame> findByUser(Users user);

    boolean existsByUserAndFrame(Users user, Frame frame);
}
