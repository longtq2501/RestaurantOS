package com.restaurantos.modules.payment.service.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.order.entity.Order;
import com.restaurantos.modules.order.repository.OrderRepository;
import com.restaurantos.modules.payment.dto.response.PaymentResponse;
import com.restaurantos.modules.payment.entity.PaymentMethod;
import com.restaurantos.modules.payment.entity.Transaction;
import com.restaurantos.modules.payment.entity.TransactionStatus;
import com.restaurantos.modules.payment.repository.TransactionRepository;
import com.restaurantos.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoPaymentServiceImpl {

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public PaymentResponse initiatePayment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        log.info("Initiating Momo payment for order: {}", order.getOrderNumber());

        // In a real implementation, you would:
        // 1. Prepare Momo request (amount, orderInfo, requestId, etc.)
        // 2. Generate HmacSHA256 signature
        // 3. Call Momo API (POST /v2/gateway/api/create)
        // 4. Return the payUrl

        Transaction transaction = Transaction.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(PaymentMethod.MOMO)
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepository.save(transaction);

        // Mocking Momo response for now
        return PaymentResponse.builder()
                .orderId(orderId)
                .orderNumber(order.getOrderNumber())
                .amount(order.getTotalAmount())
                .status(TransactionStatus.PENDING)
                .paymentUrl("https://test-payment.momo.vn/pay/..." + UUID.randomUUID())
                .message("Forwarding to Momo sandbox")
                .build();
    }

    @Transactional
    public Transaction handleCallback(Map<String, String> data) {
        // Validation logic for Momo signature would go here
        String orderIdStr = data.get("orderId"); // This is usually our orderNumber or a special ID
        String errorCode = data.get("resultCode");
        String transId = data.get("transId");

        // Example logic:
        // if ("0".equals(errorCode)) { ... COMPLETED ... } else { ... FAILED ... }

        // This would be called by the orchestrator
        return null;
    }
}
