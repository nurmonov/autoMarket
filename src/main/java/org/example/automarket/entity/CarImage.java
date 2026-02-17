package org.example.automarket.entity;

import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class CarImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imageUrl; // Rasmni manzili (path yoki URL)

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
}
