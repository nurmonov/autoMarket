package org.example.automarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandUpdateRequest {

    private String name;      // optional
    private String logoUrl;   // optional
}
