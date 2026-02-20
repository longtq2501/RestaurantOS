package com.restaurantos.modules.analytics.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.analytics.dto.response.DashboardSummaryResponse;
import com.restaurantos.modules.analytics.dto.response.TopDishResponse;
import com.restaurantos.modules.analytics.service.DashboardService;
import com.restaurantos.modules.inventory.repository.IngredientRepository;
import com.restaurantos.modules.order.entity.Order;
import com.restaurantos.modules.order.repository.OrderItemRepository;
import com.restaurantos.modules.order.repository.OrderRepository;
import com.restaurantos.modules.table.repository.TableRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of DashboardService.
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final IngredientRepository ingredientRepository;
    private final TableRepository tableRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(UUID restaurantId) {
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        LocalDateTime startOfYesterday = startOfToday.minusDays(1);
        LocalDateTime endOfYesterday = endOfToday.minusDays(1);

        // Today's metrics
        List<Order> todayOrders = orderRepository.findByRestaurantIdAndCreatedAtBetween(restaurantId, startOfToday,
                endOfToday);
        BigDecimal todayRevenue = todayOrders.stream()
                .filter(o -> o.getStatus().name().equals("COMPLETED"))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Yesterday's metrics for growth rate
        List<Order> yesterdayOrders = orderRepository.findByRestaurantIdAndCreatedAtBetween(restaurantId,
                startOfYesterday, endOfYesterday);
        BigDecimal yesterdayRevenue = yesterdayOrders.stream()
                .filter(o -> o.getStatus().name().equals("COMPLETED"))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Double growthRate = 0.0;
        if (yesterdayRevenue.compareTo(BigDecimal.ZERO) > 0) {
            growthRate = todayRevenue.subtract(yesterdayRevenue)
                    .divide(yesterdayRevenue, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // Top 5 dishes today
        List<TopDishResponse> topDishes = orderItemRepository.findTopSellingItems(
                restaurantId, startOfToday, endOfToday, PageRequest.of(0, 5));

        // Low stock count
        int lowStockCount = ingredientRepository.findByRestaurantIdAndCurrentStockLessThanMinStock(restaurantId).size();

        // Active tables count - simplified for now, assuming tables are active if they
        // have an associated currentOrderId
        // In a real scenario, we might need a more precise definition
        long activeTablesCount = tableRepository.findByRestaurantIdOrderByTableNumberAsc(restaurantId).stream()
                .filter(t -> t.getStatus().name().equals("OCCUPIED"))
                .count();

        return DashboardSummaryResponse.builder()
                .todayRevenue(todayRevenue)
                .todayOrders(todayOrders.size())
                .revenueGrowthRate(growthRate)
                .topDishes(topDishes)
                .lowStockAlertsCount(lowStockCount)
                .activeTablesCount((int) activeTablesCount)
                .build();
    }
}
