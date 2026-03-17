package org.example.automarket.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {

    // Umumiy (barcha vaqt uchun)
    private TimePeriodStats overall;

    // Tanlangan yil bo‘yicha
    private Integer selectedYear;
    private TimePeriodStats yearlyStats;

    // Tanlangan oy bo‘yicha (agar tanlangan bo‘lsa)
    private Integer selectedMonth; // 1 = Yanvar, 12 = Dekabr
    private TimePeriodStats monthlyStats;

    // Yil tanlanganda chiqadigan oylar statistikasi (har bir oy uchun qisqa)
    private List<MonthSummary> monthlySummaries;
}
