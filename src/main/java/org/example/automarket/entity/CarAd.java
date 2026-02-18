package org.example.automarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.awt.*;

import org.example.automarket.entity.enums.*;
import org.example.automarket.entity.enums.Color;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car_ads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CarAd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Model model;

    private Integer year;  // 2022...

    private BigDecimal price;  // Narx, UZS yoki USD (currency qo'shsa bo'ladi keyin)

    private Integer mileage;  // KM

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    private Transmission transmission;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @Lob  // Uzun tavsif uchun
    private String description;

    private String vin;  // VIN raqami (optional)

    private String stateNumber;  // Davlat raqami (optional)

    @Enumerated(EnumType.STRING)
    private AdStatus status = AdStatus.PENDING;

    private boolean isFeatured = false;  // Featured e'lon (pullik?)

    @OneToMany(mappedBy = "carAd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarImage> images = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime soldAt;
}