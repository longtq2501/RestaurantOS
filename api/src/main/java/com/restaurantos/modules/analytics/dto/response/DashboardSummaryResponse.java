package com.restaurantos.modules.analytics.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for dashboard summary metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private BigDecimal todayRevenue;
    private Integer todayOrders;
    private Double revenueGrowthRate;
    private List<TopDishResponse> topDishes;
    private Integer lowStockAlertsCount;
    private Integer activeTablesCount;
}
