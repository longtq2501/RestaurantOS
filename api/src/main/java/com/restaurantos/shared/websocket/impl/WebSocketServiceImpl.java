package com.restaurantos.shared.websocket.impl;

import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.restaurantos.shared.websocket.WebSocketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of WebSocketService using SimpMessagingTemplate.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void broadcastToKitchen(UUID restaurantId, Object payload) {
        String destination = "/topic/restaurants/" + restaurantId + "/kitchen";
        log.debug("Broadcasting to kitchen {}: {}", restaurantId, payload);
        messagingTemplate.convertAndSend(destination, payload);
    }

    @Override
    public void broadcastToOrder(UUID orderId, Object payload) {
        String destination = "/topic/orders/" + orderId;
        log.debug("Broadcasting to order {}: {}", orderId, payload);
        messagingTemplate.convertAndSend(destination, payload);
    }

    @Override
    public void broadcastToDashboard(UUID restaurantId, Object payload) {
        String destination = "/topic/restaurants/" + restaurantId + "/dashboard";
        log.debug("Broadcasting to dashboard {}: {}", restaurantId, payload);
        messagingTemplate.convertAndSend(destination, payload);
    }
}
