package org.example.automarket.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "car_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private CarAd carAd;

    @Column(nullable = false)
    private String imageUrl;  // Cloudinary URL yoki path

    private boolean isMain = false;  // Asosiy rasm

    private Integer orderIndex = 0;  // Tartib
}
