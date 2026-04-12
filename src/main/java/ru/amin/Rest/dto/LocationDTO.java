package ru.amin.Rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LocationDTO {
    private double lat;
    private double lng;
    private LocalDateTime timestamp;

    // Уровень заряда батареи (0-100), необязательное поле
    private Integer batteryLevel;
}
