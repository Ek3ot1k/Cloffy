package ru.amin.Rest.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationDTO {

    @Size(max = 200, message = "Название школы не может быть длиннее 200 символов")
    private String school;

    @Size(max = 200, message = "Название университета не может быть длиннее 200 символов")
    private String university;
}
