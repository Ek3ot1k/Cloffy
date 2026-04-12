package ru.amin.Rest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "stories")
@Getter
@Setter
@NoArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "caption")
    private String caption;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // История активна 24 часа
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
