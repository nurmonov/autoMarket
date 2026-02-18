package org.example.automarket.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.automarket.entity.enums.AdStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarAdUpdateRequest {
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

}