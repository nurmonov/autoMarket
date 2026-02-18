package org.example.automarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCarAdPendingDto {
    private Long id;
    private String brandName;
    private String modelName;
    private Integer year;
    private BigDecimal price;
    private String sellerPhone;
    private String sellerFullName;
    private LocalDateTime createdAt;
}
