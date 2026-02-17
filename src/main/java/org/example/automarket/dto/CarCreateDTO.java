package org.example.automarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.automarket.entity.enums.BodyType;
import org.example.automarket.entity.enums.Color;
import org.example.automarket.entity.enums.FuelType;
import org.example.automarket.entity.enums.Transmission;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarCreateDTO {
    private Integer brandId;
    private Integer modelId;
    private BodyType bodyType;
    private Color color;
    private Integer year;
    private Integer price;
    private Integer distance;
    private Transmission transmission;
    private FuelType fuelType;
    private String description;
}
