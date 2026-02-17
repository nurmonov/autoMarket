package org.example.automarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.automarket.entity.enums.CarStatus;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarAdminDTO {
    private Integer id;
    private String carName; // Masalan: "Toyota Camry"
    private Integer price;
    private CarStatus status;
}
