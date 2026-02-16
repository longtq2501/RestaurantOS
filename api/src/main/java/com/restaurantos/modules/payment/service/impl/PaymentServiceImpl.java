package com.restaurantos.modules.payment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.order.entity.Order;
import com.restaurantos.modules.order.entity.PaymentStatus;
import com.restaurantos.modules.order.repository.OrderRepository;
import com.restaurantos.modules.payment.dto.response.PaymentResponse;
import com.restaurantos.modules.payment.entity.PaymentMethod;
import com.restaurantos.modules.payment.entity.TransactionStatus;
import com.restaurantos.modules.payment.repository.TransactionRepository;
import com.restaurantos.modules.payment.service.PaymentService;
import com.restaurantos.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final MomoPaymentServiceImpl momoService;
    private final VNPayPaymentServiceImpl vnpayService;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(UUID orderId, PaymentMethod method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (method == PaymentMethod.CASH) {
            order.setPaymentStatus(PaymentStatus.PAID);
            orderRepository.save(order);
            return PaymentResponse.builder()
                    .orderId(orderId)
                    .orderNumber(order.getOrderNumber())
                    .amount(order.getTotalAmount())
                    .status(TransactionStatus.COMPLETED)
                    .message("Cash payment processed locally")
                    .build();
        }

        if (method == PaymentMethod.MOMO) {
            return momoService.initiatePayment(orderId);
        }

        if (method == PaymentMethod.VNPAY) {
            return vnpayService.initiatePayment(orderId);
        }

        throw new IllegalArgumentException("Unsupported payment method: " + method);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentStatus(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Logic to check transaction history
        // In a real app, this might involve calling the gateway API to query status

        return PaymentResponse.builder()
                .orderId(orderId)
                .orderNumber(order.getOrderNumber())
                .amount(order.getTotalAmount())
                .status(TransactionStatus.PENDING) // Default for mock
                .message("Checking status...")
                .build();
    }
}
