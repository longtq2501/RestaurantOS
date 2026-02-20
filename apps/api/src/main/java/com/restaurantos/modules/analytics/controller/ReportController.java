package com.restaurantos.modules.analytics.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurantos.modules.analytics.dto.response.RevenueReportResponse;
import com.restaurantos.modules.analytics.dto.response.TopDishResponse;
import com.restaurantos.modules.analytics.service.ReportService;
import com.restaurantos.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for analytics reports.
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/revenue")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getRevenueReport(
            @PathVariable UUID restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getRevenueReport(restaurantId, startDate, endDate)));
    }

    @GetMapping("/top-dishes")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<TopDishResponse>>> getTopDishes(
            @PathVariable UUID restaurantId,
            @RequestParam(defaultValue = "today") String period) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getTopDishes(restaurantId, period)));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<byte[]> exportReport(
            @PathVariable UUID restaurantId,
            @RequestParam(defaultValue = "csv") String format) {
        byte[] data = reportService.exportReport(restaurantId, format);

        String filename = "report-" + LocalDate.now() + "." + format;
        MediaType mediaType = format.equalsIgnoreCase("csv") ? MediaType.parseMediaType("text/csv")
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(data);
    }
}
