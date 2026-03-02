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
    private String transmission;   // "MANUAL", "AUTOMATIC"...
    private String fuelType;
    private String bodyType;
    private String mainImageUrl;
    private String sellerRegion;
    private AdStatus status;
    private LocalDateTime createdAt;
    private boolean isFavorite;     // joriy user uchun yoqtirilganmi (frontend uchun qulay)
    private String description="salom alekum hush kelibsiz ";
    private Double averageRating;

    public Double getAverageRating() {
        if (averageRating != null && averageRating > 0) {
            // Agar real qiymat bo‘lsa — uni yaxlitlaymiz
            return Math.round(averageRating * 10.0) / 10.0;
        }

        // Har safar yangi random (4.0 ... 5.0, 1 kasr belgigacha)
        double randomRaw = 4.0 + (Math.random() * 1.0);
        return Math.round(randomRaw * 10.0) / 10.0;  // 4.0, 4.1, ..., 4.9, 5.0
    }

}
