package ru.amin.Rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetRequestCreateDTO {

    @NotNull(message = "Широта точки встречи обязательна")
    private Double meetLat;

    @NotNull(message = "Долгота точки встречи обязательна")
    private Double meetLng;
}
