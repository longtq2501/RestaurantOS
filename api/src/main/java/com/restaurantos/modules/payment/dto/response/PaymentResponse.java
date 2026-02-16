package com.restaurantos.modules.payment.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.restaurantos.modules.payment.entity.TransactionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unified response for payment initiation and status checks.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID orderId;
    private String orderNumber;
    private BigDecimal amount;
    private TransactionStatus status;
    private String paymentUrl; // For redirect to gateway
    private String gatewayTransactionId;
    private String message;
}
