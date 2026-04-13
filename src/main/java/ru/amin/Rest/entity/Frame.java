package ru.amin.Rest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "frames")
@Getter
@Setter
@NoArgsConstructor
public class Frame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    // URL изображения рамки (хранится на клиенте / CDN)
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "price")
    private int price;

    @Column(name = "description")
    private String description;
}
