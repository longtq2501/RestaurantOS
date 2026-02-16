package com.restaurantos.modules.payment.service.impl;

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
public class VNPayPaymentServiceImpl {

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public PaymentResponse initiatePayment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        log.info("Initiating VNPay payment for order: {}", order.getOrderNumber());

        // In a real implementation:
        // 1. Prepare VNPay params (vnp_Version, vnp_Command, vnp_TmnCode, vnp_Amount,
        // etc.)
        // 2. Build hash string and vnp_SecureHash (HMACSHA512)
        // 3. Build final URL

        Transaction transaction = Transaction.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(PaymentMethod.VNPAY)
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepository.save(transaction);

        // Mocking VNPay response
        return PaymentResponse.builder()
                .orderId(orderId)
                .orderNumber(order.getOrderNumber())
                .amount(order.getTotalAmount())
                .status(TransactionStatus.PENDING)
                .paymentUrl("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?..." + UUID.randomUUID())
                .message("Forwarding to VNPay sandbox")
                .build();
    }
}
