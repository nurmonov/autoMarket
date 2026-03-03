package org.example.automarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.automarket.entity.enums.AdStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CarAdSummaryDto {
    private Long id;
    private String brandName;
    private String modelName;
    private Integer year;
    private BigDecimal price;
    private Integer mileage;
    private String color;
    private String transmission;
    private String fuelType;
    private String bodyType;
    private String mainImageUrl;
    private String sellerRegion;
    private AdStatus status;
    private LocalDateTime createdAt;
    private boolean isFavorite;
    private String description;
    private Double averageRating;



}
