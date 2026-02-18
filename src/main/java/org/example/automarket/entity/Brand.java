package org.example.automarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;  // Toyota, Chevrolet...

    private String logoUrl;  // Optional logo

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Model> models = new ArrayList<>();
}


