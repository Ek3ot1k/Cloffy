package ru.amin.Rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClassmateDTO {
    private int userId;
    private String username;
    private String school;
    private String university;
}
