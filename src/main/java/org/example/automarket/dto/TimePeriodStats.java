package org.example.automarket.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriodStats {
    private long totalAds;
    private long approved;
    private long pending;
    private long rejected;
    private long sold;
    private double averagePriceApproved;
}
