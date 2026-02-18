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
public class FavoriteDto {
    private Long carAdId;
    private String brandName;
    private String modelName;
    private Integer year;
    private BigDecimal price;
    private String mainImageUrl;
    private String region;
    private LocalDateTime addedAt;      // qachon yoqtirilgan
}
