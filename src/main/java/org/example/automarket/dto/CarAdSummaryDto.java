package org.example.automarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.automarket.entity.enums.AdStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;


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
    private String transmission;   // "MANUAL", "AUTOMATIC"...
    private String fuelType;
    private String bodyType;
    private String mainImageUrl;
    private String sellerRegion;
    private AdStatus status;
    private LocalDateTime createdAt;
    private boolean isFavorite;     // joriy user uchun yoqtirilganmi (frontend uchun qulay)





}
