package ru.amin.Rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.amin.Rest.util.MeetStatus;

import java.time.LocalDateTime;

// Ответ на запрос встречи — содержит точку встречи для построения маршрута на клиенте
@Getter
@AllArgsConstructor
public class MeetResponseDTO {
    private int id;
    private int requesterId;
    private String requesterName;
    private int receiverId;
    private String receiverName;
    private double meetLat;
    private double meetLng;
    private MeetStatus status;
    private LocalDateTime createdAt;
}
