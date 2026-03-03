package org.example.automarket.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatsDto {
    private long totalAds;
    private long soldAds;
    private long pendingAds;

}
