package com.restaurantos.modules.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurantos.modules.auth.entity.User;
import com.restaurantos.modules.auth.repository.UserRepository;
import com.restaurantos.modules.notification.dto.response.NotificationResponse;
import com.restaurantos.modules.notification.entity.Notification;
import com.restaurantos.modules.notification.entity.NotificationType;
import com.restaurantos.modules.notification.repository.NotificationRepository;
import com.restaurantos.modules.notification.service.impl.NotificationServiceImpl;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder().username("testuser").build();
        user.setId(userId);
    }

    @Test
    void create_ShouldReturnSavedNotification() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(UUID.randomUUID());
            return n;
        });

        // When
        Notification result = notificationService.create(userId, NotificationType.SYSTEM_ALERT, "Title", "Message",
                "/url");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Title");
        assertThat(result.getUser()).isEqualTo(user);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getUnreadNotifications_ShouldReturnMappedResponses() {
        // Given
        Notification notification = Notification.builder()
                .title("Unread")
                .isRead(false)
                .build();
        notification.setId(UUID.randomUUID());
        notification.setCreatedAt(LocalDateTime.now());

        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId))
                .thenReturn(Collections.singletonList(notification));

        // When
        List<NotificationResponse> result = notificationService.getUnreadNotifications(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Unread");
        assertThat(result.get(0).isRead()).isFalse();
    }

    @Test
    void markAsRead_ShouldUpdateStatus() {
        // Given
        UUID notificationId = UUID.randomUUID();
        Notification notification = Notification.builder()
                .isRead(false)
                .build();
        notification.setId(notificationId);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // When
        notificationService.markAsRead(notificationId);

        // Then
        assertThat(notification.isRead()).isTrue();
        assertThat(notification.getReadAt()).isNotNull();
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAllAsRead_ShouldUpdateAllUnread() {
        // Given
        Notification n1 = Notification.builder().isRead(false).build();
        Notification n2 = Notification.builder().isRead(false).build();

        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(n1, n2));

        // When
        notificationService.markAllAsRead(userId);

        // Then
        assertThat(n1.isRead()).isTrue();
        assertThat(n2.isRead()).isTrue();
        verify(notificationRepository).saveAll(any());
    }
}
