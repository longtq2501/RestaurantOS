package com.restaurantos.shared.websocket;

import java.util.UUID;

/**
 * Service for broadcasting messages via WebSocket/STOMP.
 */
public interface WebSocketService {

    /**
     * Broadcasts a message to the kitchen staff of a specific restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @param payload      the message payload
     */
    void broadcastToKitchen(UUID restaurantId, Object payload);

    /**
     * Broadcasts a message to a specific order topic.
     * 
     * @param orderId the order ID
     * @param payload the message payload
     */
    void broadcastToOrder(UUID orderId, Object payload);

    /**
     * Broadcasts a message to the dashboard of a specific restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @param payload      the message payload
     */
    void broadcastToDashboard(UUID restaurantId, Object payload);
}
