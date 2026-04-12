package ru.amin.Rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoryCreateDTO {

    // URL изображения (клиент загружает в Firebase/S3 и передаёт URL)
    @NotBlank(message = "URL изображения обязателен")
    private String imageUrl;

    private String caption;
}
