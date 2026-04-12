package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Post;
import ru.amin.Rest.entity.PostLike;
import ru.amin.Rest.entity.Users;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    Optional<PostLike> findByUserAndPost(Users user, Post post);

    int countByPost(Post post);

    boolean existsByUserAndPost(Users user, Post post);
}
