package org.example.automarket.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDto {


    private long totalAds;
    private long totalApproved;
    private long totalPending;
    private long totalRejected;
    private long totalSold;
    private long totalArchived;


    private TimeBasedStats today;
    private TimeBasedStats lastWeek;
    private TimeBasedStats lastMonth;


    private double averagePriceApproved;
    private String topBrand;
    private String topModel;
}
