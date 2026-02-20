package com.restaurantos.modules.analytics.service;

import java.util.UUID;

import com.restaurantos.modules.analytics.dto.response.DashboardSummaryResponse;

/**
 * Service for dashboard metrics and summaries.
 */
public interface DashboardService {

    /**
     * Gets a summary of dashboard metrics for today.
     * 
     * @param restaurantId the restaurant ID
     * @return the dashboard summary response
     */
    DashboardSummaryResponse getSummary(UUID restaurantId);
}
