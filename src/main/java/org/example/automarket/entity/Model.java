package org.example.automarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private String name;  // Camry, Malibu...

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    private List<CarAd> carAds = new ArrayList<>();
}