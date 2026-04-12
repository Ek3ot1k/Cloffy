package ru.amin.Rest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.amin.Rest.util.MeetStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "meet_requests")
@Getter
@Setter
@NoArgsConstructor
public class MeetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private Users requester;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private Users receiver;

    // Точка встречи
    @Column(name = "meet_lat")
    private double meetLat;

    @Column(name = "meet_lng")
    private double meetLng;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MeetStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public MeetRequest(Users requester, Users receiver, double meetLat, double meetLng) {
        this.requester = requester;
        this.receiver = receiver;
        this.meetLat = meetLat;
        this.meetLng = meetLng;
        this.status = MeetStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
