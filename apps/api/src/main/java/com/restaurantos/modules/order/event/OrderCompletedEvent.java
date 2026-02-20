package com.restaurantos.modules.order.event;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event published when an order is completed.
 */
@Getter
@AllArgsConstructor
public class OrderCompletedEvent {
    private final UUID orderId;
    private final UUID restaurantId;
}
