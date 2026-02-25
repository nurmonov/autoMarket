package org.example.automarket.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yuklangan yoki mavjud rasm haqida ma'lumot")
public class CarImageResponseDto {

    private Long id;

    private Long carAdId;

    private String imageUrl;

    private boolean isMain;

    private Integer orderIndex;
}
