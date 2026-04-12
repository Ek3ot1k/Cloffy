package ru.amin.Rest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "Users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    // @JsonIgnore предотвращает бесконечную рекурсию при сериализации
    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    private List<Message> messages;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Location> locations;

    // Дружбы, где пользователь является инициатором
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Friendship> friendships;

    // Дружбы, где пользователь является получателем запроса
    @JsonIgnore
    @OneToMany(mappedBy = "friend")
    private List<Friendship> friendsOf;

    @Column(name = "role")
    private String role;

    // Статус пользователя: HOME, WORK или кастомный текст
    @Column(name = "status")
    private String status;

    // Уровень заряда батареи устройства (0-100), обновляется при каждой отправке геолокации
    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "school")
    private String school;

    @Column(name = "university")
    private String university;

}
