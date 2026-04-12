package ru.amin.Rest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "blocks")
@Getter
@Setter
@NoArgsConstructor
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "blocker_id", referencedColumnName = "id")
    private Users blocker;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "blocked_id", referencedColumnName = "id")
    private Users blocked;

    public Block(Users blocker, Users blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }
}
