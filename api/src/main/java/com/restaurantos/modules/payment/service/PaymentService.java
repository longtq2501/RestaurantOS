package com.restaurantos.modules.payment.service;

import java.util.UUID;

import com.restaurantos.modules.payment.dto.response.PaymentResponse;
import com.restaurantos.modules.payment.entity.PaymentMethod;

/**
 * Interface for processing payments with various gateways.
 */
public interface PaymentService {

    /**
     * Initiates a payment for an order.
     * 
     * @param orderId The ID of the order to pay for
     * @param method  The selected payment method
     * @return A response containing the payment URL or status
     */
    PaymentResponse processPayment(UUID orderId, PaymentMethod method);

    /**
     * Checks the status of a transaction with the gateway.
     * 
     * @param transactionId The gateway-specific transaction ID or our internal
     *                      transaction ID
     * @return Updated payment response
     */
    PaymentResponse getPaymentStatus(UUID orderId);
}
