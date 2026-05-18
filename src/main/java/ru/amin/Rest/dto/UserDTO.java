package ru.amin.Rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDTO {
    private int id;
    private String name;
    private int age;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public UserDTO() {}

    public UserDTO(String name, int age, String email, String password) {
        this.name = name;
        this.age = age;
        this.email=email;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
