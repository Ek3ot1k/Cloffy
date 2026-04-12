package ru.amin.Rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateDTO {

    @NotBlank(message = "URL изображения обязателен")
    private String imageUrl;

    private String caption;
}
