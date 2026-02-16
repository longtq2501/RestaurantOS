package com.restaurantos.modules.notification.service;

import java.util.List;
import java.util.UUID;

import com.restaurantos.modules.notification.dto.response.NotificationResponse;
import com.restaurantos.modules.notification.entity.Notification;
import com.restaurantos.modules.notification.entity.NotificationType;

/**
 * Service for managing notifications.
 */
public interface NotificationService {

    /**
     * Creates a new notification for a specific user.
     * 
     * @param userId    the user ID
     * @param type      the notification type
     * @param title     the notification title
     * @param message   the notification message
     * @param actionUrl optional URL associated with the notification
     * @return the created notification
     */
    Notification create(UUID userId, NotificationType type, String title, String message, String actionUrl);

    /**
     * Gets all unread notifications for a specific user.
     * 
     * @param userId the user ID
     * @return a list of notification response DTOs
     */
    List<NotificationResponse> getUnreadNotifications(UUID userId);

    /**
     * Marks a specific notification as read.
     * 
     * @param notificationId the notification ID
     */
    void markAsRead(UUID notificationId);

    /**
     * Marks all notifications for a specific user as read.
     * 
     * @param userId the user ID
     */
    void markAllAsRead(UUID userId);
}
