package com.restaurantos.modules.analytics.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurantos.modules.analytics.dto.response.DashboardSummaryResponse;
import com.restaurantos.modules.analytics.service.DashboardService;
import com.restaurantos.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for dashboard metrics.
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary(restaurantId)));
    }
}
