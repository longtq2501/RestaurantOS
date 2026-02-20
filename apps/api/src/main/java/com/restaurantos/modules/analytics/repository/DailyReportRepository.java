package com.restaurantos.modules.analytics.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.analytics.entity.DailyReport;

/**
 * Repository for DailyReport entity.
 */
@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, UUID> {

    /**
     * Find a daily report by restaurant ID and date.
     * 
     * @param restaurantId the restaurant ID
     * @param date         the report date
     * @return an optional containing the daily report if found
     */
    Optional<DailyReport> findByRestaurantIdAndReportDate(UUID restaurantId, LocalDate date);

    /**
     * Finds daily reports within a date range for a specific restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @param startDate    the start date
     * @param endDate      the end date
     * @return a list of daily reports
     */
    List<DailyReport> findByRestaurantIdAndReportDateBetween(UUID restaurantId, LocalDate startDate, LocalDate endDate);
}
