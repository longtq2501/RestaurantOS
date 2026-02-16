package com.restaurantos.modules.notification.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.restaurantos.modules.notification.entity.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for notification details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private NotificationType type;
    private String title;
    private String message;
    private String actionUrl;
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
