package org.example.automarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.automarket.entity.enums.CarStatus;
import org.example.automarket.entity.enums.Color;
import org.example.automarket.entity.enums.FuelType;
import org.example.automarket.entity.enums.Transmission;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarResponseDTO {
    private Integer id;
    private String brandName;
    private String modelName;
    private Integer year;
    private Integer price;
    private Integer distance;
    private Transmission transmission;
    private FuelType fuelType;
    private Color color;
    private CarStatus status;
    private List<String> imageUrls;
    private String ownerName;
    private String ownerPhone;
}
