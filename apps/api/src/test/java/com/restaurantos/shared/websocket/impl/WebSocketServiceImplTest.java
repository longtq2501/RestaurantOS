package com.restaurantos.shared.websocket.impl;

import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class WebSocketServiceImplTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketServiceImpl webSocketService;

    private UUID restaurantId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        orderId = UUID.randomUUID();
    }

    @Test
    void broadcastToKitchen_ShouldSendToCorrectDestination() {
        // Given
        String payload = "Test Message";
        String expectedDestination = "/topic/restaurants/" + restaurantId + "/kitchen";

        // When
        webSocketService.broadcastToKitchen(restaurantId, payload);

        // Then
        verify(messagingTemplate).convertAndSend(expectedDestination, (Object) payload);
    }

    @Test
    void broadcastToOrder_ShouldSendToCorrectDestination() {
        // Given
        String payload = "Order Update";
        String expectedDestination = "/topic/orders/" + orderId;

        // When
        webSocketService.broadcastToOrder(orderId, payload);

        // Then
        verify(messagingTemplate).convertAndSend(expectedDestination, (Object) payload);
    }

    @Test
    void broadcastToDashboard_ShouldSendToCorrectDestination() {
        // Given
        String payload = "Dashboard Update";
        String expectedDestination = "/topic/restaurants/" + restaurantId + "/dashboard";

        // When
        webSocketService.broadcastToDashboard(restaurantId, payload);

        // Then
        verify(messagingTemplate).convertAndSend(expectedDestination, (Object) payload);
    }
}
