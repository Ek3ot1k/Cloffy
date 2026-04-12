package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Post;
import ru.amin.Rest.entity.PostComment;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    List<PostComment> findByPostOrderByCreatedAtAsc(Post post);
}
