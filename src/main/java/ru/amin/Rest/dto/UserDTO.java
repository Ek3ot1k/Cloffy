package ru.amin.Rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDTO {
    private String name;
    private int age;
    private String password;

    public UserDTO() {}

    public UserDTO(String name, int age, String password) {
        this.name = name;
        this.age = age;
        this.password = password;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}