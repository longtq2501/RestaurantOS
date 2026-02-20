package com.restaurantos.modules.order.entity;

/**
 * Represents the lifecycle status of an order.
 */
public enum OrderStatus {
    PENDING, // Order placed but not yet confirmed by staff
    CONFIRMED, // Staff has accepted the order
    PREPARING, // Items are being cooked/prepared
    READY, // All items are ready for serving/pickup
    COMPLETED, // Order is paid and finished
    CANCELLED // Order was cancelled
}
