package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Post;
import ru.amin.Rest.entity.Users;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // Посты друзей, отсортированные по дате (новые первые)
    List<Post> findByUserInOrderByCreatedAtDesc(List<Users> users);
}
