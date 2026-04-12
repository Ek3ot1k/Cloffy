package ru.amin.Rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusDTO {

    // Предустановленные: HOME, WORK. Также можно передать любой текст (кастомный статус)
    @NotBlank(message = "Статус не может быть пустым")
    @Size(max = 100, message = "Статус не может быть длиннее 100 символов")
    private String status;
}
