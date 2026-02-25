package org.example.automarket.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarImageDeleteResponse {
    private Long deletedImageId;
    private String message;
    private boolean success;
}
