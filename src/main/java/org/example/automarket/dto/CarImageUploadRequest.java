package org.example.automarket.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Rasm yuklash uchun so'rov (multipart/form-data)")
public class CarImageUploadRequest {

    private List<MultipartFile> images;
}
