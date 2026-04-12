package ru.amin.Rest.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.dto.CommentCreateDTO;
import ru.amin.Rest.dto.CommentResponseDTO;
import ru.amin.Rest.dto.PostCreateDTO;
import ru.amin.Rest.dto.PostResponseDTO;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.PostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Опубликовать пост
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestBody @Valid PostCreateDTO dto,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        PostResponseDTO post = postService.createPost(usersDetails.getUser(), dto);
        return ResponseEntity.ok(post);
    }

    // Лента постов друзей
    @GetMapping("/friends")
    public ResponseEntity<List<PostResponseDTO>> getFriendsPosts(
            @AuthenticationPrincipal UsersDetails usersDetails) {
        List<PostResponseDTO> posts = postService.getFriendsPosts(usersDetails.getUser());
        return ResponseEntity.ok(posts);
    }

    // Поставить лайк
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, String>> likePost(
            @PathVariable int postId,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        postService.likePost(usersDetails.getUser(), postId);
        return ResponseEntity.ok(Map.of("result", "liked"));
    }

    // Убрать лайк
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Map<String, String>> unlikePost(
            @PathVariable int postId,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        postService.unlikePost(usersDetails.getUser(), postId);
        return ResponseEntity.ok(Map.of("result", "unliked"));
    }

    // Добавить комментарий
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable int postId,
            @RequestBody @Valid CommentCreateDTO dto,
            @AuthenticationPrincipal UsersDetails usersDetails) {
        CommentResponseDTO comment = postService.addComment(usersDetails.getUser(), postId, dto);
        return ResponseEntity.ok(comment);
    }

    // Получить комментарии к посту
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable int postId) {
        List<CommentResponseDTO> comments = postService.getComments(postId);
        return ResponseEntity.ok(comments);
    }
}
