package org.example.automarket.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponseDto {
    private Long id;
    private String name;
    private String logoUrl;     // agar logo bo'lsa
}