package com.restaurantos.modules.analytics.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.restaurantos.modules.analytics.dto.response.RevenueReportResponse;
import com.restaurantos.modules.analytics.dto.response.TopDishResponse;

/**
 * Service for generating analytics reports.
 */
public interface ReportService {

    /**
     * Gets a revenue report for a specific date range.
     * 
     * @param restaurantId the restaurant ID
     * @param startDate    the start date
     * @param endDate      the end date
     * @return the revenue report response
     */
    RevenueReportResponse getRevenueReport(UUID restaurantId, LocalDate startDate, LocalDate endDate);

    /**
     * Gets a list of top-selling dishes for a specific period.
     * 
     * @param restaurantId the restaurant ID
     * @param period       the period (e.g., "today", "yesterday", "last_7_days",
     *                     "last_30_days", "month")
     * @return a list of top dish responses
     */
    List<TopDishResponse> getTopDishes(UUID restaurantId, String period);

    /**
     * Exports a report in a specific format.
     * 
     * @param restaurantId the restaurant ID
     * @param format       the export format (e.g., "csv", "excel")
     * @return the exported report as a byte array
     */
    byte[] exportReport(UUID restaurantId, String format);
}
