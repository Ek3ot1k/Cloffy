package ru.amin.Rest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_frames")
@Getter
@Setter
@NoArgsConstructor
public class UserFrame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "frame_id", referencedColumnName = "id")
    private Frame frame;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    public UserFrame(Users user, Frame frame) {
        this.user = user;
        this.frame = frame;
        this.purchasedAt = LocalDateTime.now();
    }
}
