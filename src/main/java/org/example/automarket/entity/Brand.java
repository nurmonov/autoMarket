package org.example.automarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name; // Masalan: "Chevrolet", "BMW"

    @OneToMany(mappedBy = "brand")
    private List<Model> models;
}


