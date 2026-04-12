package ru.amin.Rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// Геолокация друга, возвращаемая в REST-ответе
@Getter
@AllArgsConstructor
public class FriendLocationDTO {
    private int userId;
    private String username;
    private double lat;
    private double lng;
    private LocalDateTime updatedAt;
    private Integer batteryLevel;
}
