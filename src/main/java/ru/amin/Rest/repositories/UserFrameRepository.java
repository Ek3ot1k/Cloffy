package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Frame;
import ru.amin.Rest.entity.UserFrame;
import ru.amin.Rest.entity.Users;

import java.util.List;

@Repository
public interface UserFrameRepository extends JpaRepository<UserFrame, Integer> {

    List<UserFrame> findByUser(Users user);

    @Query("SELECT uf FROM UserFrame uf JOIN FETCH uf.frame WHERE uf.user = :user")
    List<UserFrame> findByUserWithFrame(@Param("user") Users user);

    boolean existsByUserAndFrame(Users user, Frame frame);
}
