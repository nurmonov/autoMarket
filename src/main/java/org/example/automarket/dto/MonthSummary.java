package org.example.automarket.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummary {
    private int month; // 1-12
    private String monthName;
    private long totalAds;
    private long approved;
}