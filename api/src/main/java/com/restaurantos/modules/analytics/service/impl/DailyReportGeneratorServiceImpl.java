package com.restaurantos.modules.analytics.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurantos.modules.analytics.dto.response.TopDishResponse;
import com.restaurantos.modules.analytics.entity.DailyReport;
import com.restaurantos.modules.analytics.repository.DailyReportRepository;
import com.restaurantos.modules.analytics.service.DailyReportGeneratorService;
import com.restaurantos.modules.order.entity.Order;
import com.restaurantos.modules.order.repository.OrderItemRepository;
import com.restaurantos.modules.order.repository.OrderRepository;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of DailyReportGeneratorService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DailyReportGeneratorServiceImpl implements DailyReportGeneratorService {

    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DailyReportRepository dailyReportRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Scheduled(cron = "0 0 1 * * *") // Runs at 1 AM every day
    @Transactional
    public void generateDailyReports() {
        log.info("Starting daily report generation for all restaurants");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Restaurant> restaurants = restaurantRepository.findAll();

        for (Restaurant restaurant : restaurants) {
            try {
                generateReportForRestaurant(restaurant.getId(), yesterday);
            } catch (Exception e) {
                log.error("Failed to generate daily report for restaurant: {}", restaurant.getId(), e);
            }
        }
        log.info("Finished daily report generation");
    }

    @Override
    @Transactional
    public void generateReportForRestaurant(UUID restaurantId, LocalDate date) {
        log.info("Generating report for restaurant {} on date {}", restaurantId, date);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + restaurantId));

        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

        List<Order> dayOrders = orderRepository.findByRestaurantIdAndCreatedAtBetween(restaurantId, startOfDay,
                endOfDay);

        List<Order> completedOrders = dayOrders.stream()
                .filter(o -> o.getStatus().name().equals("COMPLETED"))
                .toList();

        BigDecimal totalRevenue = completedOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = completedOrders.size();

        BigDecimal avgOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Top dishes
        List<TopDishResponse> topDishes = orderItemRepository.findTopSellingItems(
                restaurantId, startOfDay, endOfDay, PageRequest.of(0, 10));

        String topSellingItemsJson = null;
        try {
            topSellingItemsJson = objectMapper.writeValueAsString(topDishes);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize top dishes to JSON", e);
        }

        // Simplified customer counts for now
        // In a real scenario, this would involve more complex logic based on
        // customerPhone or unique customer identifiers
        long totalCustomers = dayOrders.stream()
                .map(Order::getCustomerPhone)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();

        // Create or update daily report
        DailyReport report = dailyReportRepository.findByRestaurantIdAndReportDate(restaurantId, date)
                .orElse(DailyReport.builder()
                        .restaurant(restaurant)
                        .reportDate(date)
                        .build());

        report.setTotalRevenue(totalRevenue);
        report.setTotalOrders(totalOrders);
        report.setAvgOrderValue(avgOrderValue);
        report.setTopSellingItems(topSellingItemsJson);
        report.setTotalCustomers((int) totalCustomers);
        // newCustomers and avgPrepTime could be calculated here as well

        dailyReportRepository.save(report);
    }
}
