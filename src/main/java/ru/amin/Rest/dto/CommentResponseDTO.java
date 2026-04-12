package ru.amin.Rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponseDTO {
    private int id;
    private int userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
}
