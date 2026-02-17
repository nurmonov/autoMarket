package org.example.automarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
import org.example.automarket.entity.enums.BodyType;
import org.example.automarket.entity.enums.CarStatus;
import org.example.automarket.entity.enums.Color;
import org.example.automarket.entity.enums.FuelType;
import org.example.automarket.entity.enums.Transmission;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Brand brand;

    @ManyToOne
    private Model model;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType; // Sedan, SUV...

    @Enumerated(EnumType.STRING)
    private Color color; // Ranglar uchun

    private Integer year;
    private Integer price;
    private Integer distance;

    @Enumerated(EnumType.STRING)
    private Transmission transmission;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    private CarStatus status = CarStatus.PENDING; // Default holatda kutilmoqda

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<CarImage> images;
}
