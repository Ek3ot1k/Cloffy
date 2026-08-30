package ru.amin.Rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoryCreateDTO {

    // URL изображения (клиент загружает в Firebase/S3 и передаёт URL)
    @NotBlank(message = "URL изображения обязателен")
    @Size(max = 2048, message = "URL изображения не может быть длиннее 2048 символов")
    private String imageUrl;

    private String caption;
}
