package org.example.automarket.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarAdSearchRequest {
    private Long brandId;
    private Long modelId;
    private Integer minYear;
    private Integer maxYear;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String color;
    private String transmission;
    private String fuelType;
    private String bodyType;
    private String region;

}
