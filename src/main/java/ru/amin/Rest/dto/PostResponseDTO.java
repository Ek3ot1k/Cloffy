package ru.amin.Rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponseDTO {
    private int id;
    private int userId;
    private String username;
    private String imageUrl;
    private String caption;
    private LocalDateTime createdAt;
    private int likesCount;
    private boolean likedByMe;
    private int commentsCount;
}
