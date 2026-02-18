package org.example.automarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelResponseDto {
    private Long id;
    private String name;
    private Long brandId;
    private String brandName;   // qulaylik uchun
}
