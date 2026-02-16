package com.restaurantos.modules.notification.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurantos.modules.auth.entity.User;
import com.restaurantos.modules.notification.dto.response.NotificationResponse;
import com.restaurantos.modules.notification.service.NotificationService;
import com.restaurantos.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnread(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadNotifications(user.getId())));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
