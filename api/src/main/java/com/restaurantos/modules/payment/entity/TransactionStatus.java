package com.restaurantos.modules.payment.entity;

/**
 * Represents the lifecycle status of a payment transaction.
 */
public enum TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}
