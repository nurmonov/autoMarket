package org.example.automarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "E'lon uchun barcha rasmlar ro'yxati")
public class CarImageListResponse {
    private Long carAdId;
    private int totalImages;
    private String mainImageUrl;
    private List<CarImageResponseDto> images;
}