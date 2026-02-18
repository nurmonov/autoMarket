package org.example.automarket.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatsDto {
    private long totalAds;     // Umumiy e'lonlar
    private long soldAds;      // Sotilganlar
    private long pendingAds;   // Moderatsiyada kutayotganlar

}
