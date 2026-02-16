package com.restaurantos.modules.analytics.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.analytics.dto.response.RevenueReportResponse;
import com.restaurantos.modules.analytics.dto.response.TopDishResponse;
import com.restaurantos.modules.analytics.entity.DailyReport;
import com.restaurantos.modules.analytics.repository.DailyReportRepository;
import com.restaurantos.modules.analytics.service.ReportService;
import com.restaurantos.modules.order.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of ReportService.
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final DailyReportRepository dailyReportRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponse getRevenueReport(UUID restaurantId, LocalDate startDate, LocalDate endDate) {
        List<DailyReport> reports = dailyReportRepository.findByRestaurantIdAndReportDateBetween(restaurantId,
                startDate, endDate);

        BigDecimal totalRevenue = reports.stream()
                .map(DailyReport::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = reports.stream()
                .mapToInt(DailyReport::getTotalOrders)
                .sum();

        List<RevenueReportResponse.DailyRevenue> dailyData = reports.stream()
                .map(r -> RevenueReportResponse.DailyRevenue.builder()
                        .date(r.getReportDate())
                        .revenue(r.getTotalRevenue())
                        .orders(r.getTotalOrders())
                        .build())
                .collect(Collectors.toList());

        return RevenueReportResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .dailyData(dailyData)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopDishResponse> getTopDishes(UUID restaurantId, String period) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        switch (period.toLowerCase()) {
            case "yesterday":
                start = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN);
                end = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
                break;
            case "last_7_days":
                start = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
                break;
            case "last_30_days":
                start = LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.MIN);
                break;
            case "month":
                start = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
                break;
            case "today":
            default:
                start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                break;
        }

        return orderItemRepository.findTopSellingItems(restaurantId, start, end, PageRequest.of(0, 10));
    }

    @Override
    public byte[] exportReport(UUID restaurantId, String format) {
        // Simple mock implementation for now
        // In a real scenario, this would generate a CSV or Excel file
        String csvContent = "Report Date,Revenue,Orders\n";
        List<DailyReport> reports = dailyReportRepository.findByRestaurantIdAndReportDateBetween(
                restaurantId, LocalDate.now().minusMonths(1), LocalDate.now());

        for (DailyReport report : reports) {
            csvContent += String.format("%s,%s,%d\n",
                    report.getReportDate(), report.getTotalRevenue(), report.getTotalOrders());
        }

        return csvContent.getBytes();
    }
}
