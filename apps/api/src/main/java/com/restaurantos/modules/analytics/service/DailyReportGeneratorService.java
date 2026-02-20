package com.restaurantos.modules.analytics.service;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service for generating daily reports.
 */
public interface DailyReportGeneratorService {

    /**
     * Generates a daily report for all restaurants for the previous day.
     * This is intended to be called by a scheduled job.
     */
    void generateDailyReports();

    /**
     * Generates a daily report for a specific restaurant and date.
     * 
     * @param restaurantId the restaurant ID
     * @param date         the date to generate the report for
     */
    void generateReportForRestaurant(UUID restaurantId, LocalDate date);
}
