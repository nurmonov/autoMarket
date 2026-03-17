package org.example.automarket.service;


import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.*;
import org.example.automarket.entity.enums.AdStatus;
import org.example.automarket.repo.CarAdRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CarAdRepository carAdRepository;

    @Transactional(readOnly = true)
    public AdminStatsDto getAdminStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.minusDays(30);

        AdminStatsDto stats = AdminStatsDto.builder().build();

        // Umumiy
        stats.setTotalAds(carAdRepository.count());
        stats.setTotalApproved(carAdRepository.countByStatus(AdStatus.APPROVED));
        stats.setTotalPending(carAdRepository.countByStatus(AdStatus.PENDING));
        stats.setTotalRejected(carAdRepository.countByStatus(AdStatus.REJECTED));
        stats.setTotalSold(carAdRepository.countByStatus(AdStatus.SOLD));

        // Bugun
        stats.setToday(buildTimeStats(todayStart, now));

        // Oxirgi hafta
        stats.setLastWeek(buildTimeStats(weekStart, now));

        // Oxirgi oy
        stats.setLastMonth(buildTimeStats(monthStart, now));

        // Qo‘shimcha: APPROVED lar o‘rtacha narxi
        Double avgPrice = carAdRepository.findAveragePriceByStatus(AdStatus.APPROVED);
        stats.setAveragePriceApproved(avgPrice != null ? avgPrice : 0.0);

        // Eng ko‘p sotilgan brend/model (masalan TOP 1)
        // Bu uchun alohida query kerak bo‘ladi, hozircha oddiy qoldiramiz

        return stats;
    }

    private TimeBasedStats buildTimeStats(LocalDateTime start, LocalDateTime end) {
        TimeBasedStats stats = new TimeBasedStats();
        stats.setTotal(carAdRepository.countByDateRange(start, end));
        stats.setApproved(carAdRepository.countByDateRangeAndStatus(start, end, AdStatus.APPROVED));
        stats.setPending(carAdRepository.countByDateRangeAndStatus(start, end, AdStatus.PENDING));
        stats.setRejected(carAdRepository.countByDateRangeAndStatus(start, end, AdStatus.REJECTED));
        stats.setSold(carAdRepository.countByDateRangeAndStatus(start, end, AdStatus.SOLD));
        return stats;
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getAdminStatsFull(Integer year, Integer month) {
        AdminStatsResponse response = AdminStatsResponse.builder().build();

        // 1. Har doim umumiy statistika chiqadi (barcha vaqt uchun)
        response.setOverall(buildStats(null, null));

        // 2. Agar yil tanlangan bo‘lsa — shu yil statistikasi + oylar ro‘yxati
        if (year != null) {
            LocalDateTime yearStart = LocalDate.of(year, 1, 1).atStartOfDay();
            LocalDateTime yearEnd = LocalDate.of(year, 12, 31).atTime(23, 59, 59);

            response.setSelectedYear(year);
            response.setYearlyStats(buildStats(yearStart, yearEnd));

            // Har bir oy uchun qisqa statistika (frontendda dropdown/kalendar uchun)
            List<MonthSummary> months = new ArrayList<>();
            for (int m = 1; m <= 12; m++) {
                LocalDateTime monthStart = LocalDate.of(year, m, 1).atStartOfDay();
                LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);

                long total = carAdRepository.countByDateRange(monthStart, monthEnd);
                long approved = carAdRepository.countByDateRangeAndStatus(monthStart, monthEnd, AdStatus.APPROVED);

                months.add(MonthSummary.builder()
                        .month(m)
                        .monthName(getMonthName(m))
                        .totalAds(total)
                        .approved(approved)
                        .build());
            }
            response.setMonthlySummaries(months);
        }

        // 3. Agar oy ham tanlangan bo‘lsa (yil bilan birga) — shu oy statistikasi
        if (year != null && month != null && month >= 1 && month <= 12) {
            LocalDateTime monthStart = LocalDate.of(year, month, 1).atStartOfDay();
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);

            response.setSelectedMonth(month);
            response.setMonthlyStats(buildStats(monthStart, monthEnd));
        }

        // Agar yil tanlanmagan bo‘lsa — faqat umumiy chiqadi (oy ham bo‘lmaydi)

        return response;
    }

    private TimePeriodStats buildStats(LocalDateTime start, LocalDateTime end) {
        TimePeriodStats stats = new TimePeriodStats();
        stats.setTotalAds(carAdRepository.countByDateRange(start, end));
        stats.setApproved(carAdRepository.countByDateRangeAndStatus(start, end, AdStatus.APPROVED));
        stats.setPending(carAdRepository.countByDateRangeAndStatus(start, end, AdStatus.PENDING));
        stats.setRejected(carAdRepository.countByDateRangeAndStatus(start, end, AdStatus.REJECTED));
        stats.setSold(carAdRepository.countByDateRangeAndStatus(start, end, AdStatus.SOLD));

        Double avgPrice = carAdRepository.findAveragePriceByStatusAndDateRange( start, end,AdStatus.APPROVED);
        stats.setAveragePriceApproved(avgPrice != null ? avgPrice : 0.0);

        return stats;
    }

    private String getMonthName(int month) {
        return switch (month) {
            case 1 -> "Yanvar";
            case 2 -> "Fevral";
            case 3 -> "Mart";
            case 4 -> "Aprel";
            case 5 -> "May";
            case 6 -> "Iyun";
            case 7 -> "Iyul";
            case 8 -> "Avgust";
            case 9 -> "Sentabr";
            case 10 -> "Oktabr";
            case 11 -> "Noyabr";
            case 12 -> "Dekabr";
            default -> "Noma'lum";
        };
    }

    public AdminStatsDto getFullStats() {

        Object[] row = carAdRepository.getFullAdminStats();

        return AdminStatsDto.builder()
                .totalAds(((Number) row[0]).longValue())
                .totalApproved(((Number) row[1]).longValue())
                .totalPending(((Number) row[2]).longValue())
                .totalRejected(((Number) row[3]).longValue())
                .totalSold(((Number) row[4]).longValue())
                .averagePriceApproved(
                        row[5] != null ? ((Number) row[5]).doubleValue() : 0.0
                )
//                .totalAds(((Number) row[6]).longValue())
//                .lastWeekAds(((Number) row[7]).longValue())
//                .lastMonthAds(((Number) row[8]).longValue())
                .build();
    }

}
