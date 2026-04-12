package ru.amin.Rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {

    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(max = 500, message = "Комментарий не может быть длиннее 500 символов")
    private String content;
}
