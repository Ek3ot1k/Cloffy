package ru.amin.Rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiChatRequestDTO {

    @NotBlank(message = "Сообщение не может быть пустым")
    @Size(max = 1000, message = "Сообщение не может быть длиннее 1000 символов")
    private String message;

    // Координаты пользователя для контекстных рекомендаций (необязательно)
    private Double lat;
    private Double lng;
}
