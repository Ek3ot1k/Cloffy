package ru.amin.Rest.dto;

import lombok.Getter;
import lombok.Setter;

// DTO для отправки сообщения через WebSocket
@Getter
@Setter
public class ChatMessageDTO {
    private int receiverId;
    private String content;
}
