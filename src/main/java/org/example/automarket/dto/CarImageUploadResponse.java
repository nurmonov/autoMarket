package org.example.automarket.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bir nechta rasm yuklanganidan keyin qaytariladigan javob")
public class CarImageUploadResponse {

    private int uploadedCount;

    private List<CarImageResponseDto> images;
    private String message;
    private List<String> imageUrls;
}
