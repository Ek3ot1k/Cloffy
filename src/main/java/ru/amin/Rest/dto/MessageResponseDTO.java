package ru.amin.Rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// DTO для отдачи сообщений в REST-ответе
@Getter
@AllArgsConstructor
public class MessageResponseDTO {
    private int id;
    private int senderId;
    private String senderName;
    private int receiverId;
    private String content;
    private LocalDateTime timestamp;
}
