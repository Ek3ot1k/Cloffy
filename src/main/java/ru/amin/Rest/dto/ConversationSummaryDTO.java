package ru.amin.Rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// Краткое описание диалога: собеседник + последнее сообщение
@Getter
@AllArgsConstructor
public class ConversationSummaryDTO {
    private int partnerId;
    private String partnerUsername;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
}
