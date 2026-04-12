package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.amin.Rest.dto.CommentCreateDTO;
import ru.amin.Rest.dto.CommentResponseDTO;
import ru.amin.Rest.dto.PostCreateDTO;
import ru.amin.Rest.dto.PostResponseDTO;
import ru.amin.Rest.entity.Post;
import ru.amin.Rest.entity.PostComment;
import ru.amin.Rest.entity.PostLike;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.PostCommentRepository;
import ru.amin.Rest.repositories.PostLikeRepository;
import ru.amin.Rest.repositories.PostRepository;
import ru.amin.Rest.util.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final FriendshipService friendshipService;

    public PostService(PostRepository postRepository,
                       PostLikeRepository postLikeRepository,
                       PostCommentRepository postCommentRepository,
                       FriendshipService friendshipService) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.postCommentRepository = postCommentRepository;
        this.friendshipService = friendshipService;
    }

    // Создать пост
    @Transactional
    public PostResponseDTO createPost(Users user, PostCreateDTO dto) {
        Post post = new Post();
        post.setUser(user);
        post.setImageUrl(dto.getImageUrl());
        post.setCaption(dto.getCaption());
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        return toDTO(post, user);
    }

    // Лента постов друзей (новые первые)
    public List<PostResponseDTO> getFriendsPosts(Users currentUser) {
        List<Users> friends = friendshipService.getAcceptedFriends(currentUser);
        if (friends.isEmpty()) return List.of();

        return postRepository.findByUserInOrderByCreatedAtDesc(friends)
                .stream()
                .map(post -> toDTO(post, currentUser))
                .toList();
    }

    // Поставить лайк
    @Transactional
    public void likePost(Users user, int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UserNotFoundException("Пост не найден"));

        if (!postLikeRepository.existsByUserAndPost(user, post)) {
            postLikeRepository.save(new PostLike(user, post));
        }
    }

    // Убрать лайк
    @Transactional
    public void unlikePost(Users user, int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UserNotFoundException("Пост не найден"));

        postLikeRepository.findByUserAndPost(user, post)
                .ifPresent(postLikeRepository::delete);
    }

    // Добавить комментарий
    @Transactional
    public CommentResponseDTO addComment(Users user, int postId, CommentCreateDTO dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UserNotFoundException("Пост не найден"));

        PostComment comment = new PostComment(user, post, dto.getContent());
        postCommentRepository.save(comment);

        return toCommentDTO(comment);
    }

    // Получить комментарии к посту
    public List<CommentResponseDTO> getComments(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UserNotFoundException("Пост не найден"));

        return postCommentRepository.findByPostOrderByCreatedAtAsc(post)
                .stream()
                .map(this::toCommentDTO)
                .toList();
    }

    private PostResponseDTO toDTO(Post post, Users currentUser) {
        return new PostResponseDTO(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getName(),
                post.getImageUrl(),
                post.getCaption(),
                post.getCreatedAt(),
                postLikeRepository.countByPost(post),
                postLikeRepository.existsByUserAndPost(currentUser, post),
                postCommentRepository.findByPostOrderByCreatedAtAsc(post).size()
        );
    }

    private CommentResponseDTO toCommentDTO(PostComment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
