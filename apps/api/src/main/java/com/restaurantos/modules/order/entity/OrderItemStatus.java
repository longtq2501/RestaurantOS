package com.restaurantos.modules.order.entity;

/**
 * Represents the preparation status of an individual item in an order.
 */
public enum OrderItemStatus {
    PENDING, // Item is in the order but prep not started
    PREPARING, // Item is being prepared in the kitchen
    READY, // Item is ready to be served
    SERVED // Item has been brought to the table
}
