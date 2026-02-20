package com.restaurantos.modules.analytics.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.shared.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a daily aggregated report for a restaurant.
 */
@Entity
@Table(name = "daily_reports", uniqueConstraints = @UniqueConstraint(columnNames = { "restaurant_id",
        "report_date" }), indexes = {
                @Index(name = "idx_daily_report_restaurant", columnList = "restaurant_id"),
                @Index(name = "idx_daily_report_date", columnList = "report_date")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "restaurant")
public class DailyReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "total_revenue", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "total_orders", nullable = false)
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "avg_order_value", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal avgOrderValue = BigDecimal.ZERO;

    /**
     * Stores top selling items in JSON format.
     * Example: [{"id": "...", "name": "...", "quantity": 10}, ...]
     */
    @Column(name = "top_selling_items", columnDefinition = "JSON")
    private String topSellingItems;

    @Column(name = "total_customers", nullable = false)
    @Builder.Default
    private Integer totalCustomers = 0;

    @Column(name = "new_customers", nullable = false)
    @Builder.Default
    private Integer newCustomers = 0;

    @Column(name = "avg_prep_time")
    private Double avgPrepTime;
}
