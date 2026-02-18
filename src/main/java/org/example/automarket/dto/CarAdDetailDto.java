package org.example.automarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.automarket.entity.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarAdDetailDto {
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
    private String description;
    private String vin;
    private String stateNumber;
    private AdStatus status;
    private List<String> imageUrls;      // barcha rasmlar (main birinchi bo'lishi mumkin)
    private UserResponseDto seller;      // sotuvchi haqida minimal info
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private boolean isFavorite;

    // constructors...
}
