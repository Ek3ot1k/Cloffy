package ru.amin.Rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Уведомление о том, что рядом находится незнакомый пользователь
@Getter
@AllArgsConstructor
public class ProximityNotificationDTO {
    private int userId;
    private String username;
    private double distanceMeters; // расстояние в метрах
}
