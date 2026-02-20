package com.restaurantos.modules.payment.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurantos.modules.payment.dto.request.MomoPaymentRequest;
import com.restaurantos.modules.payment.dto.request.VNPayPaymentRequest;
import com.restaurantos.modules.payment.dto.response.PaymentResponse;
import com.restaurantos.modules.payment.entity.PaymentMethod;
import com.restaurantos.modules.payment.service.PaymentService;
import com.restaurantos.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for processing payments.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/momo/initiate")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiateMomo(@Valid @RequestBody MomoPaymentRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success(paymentService.processPayment(request.getOrderId(), PaymentMethod.MOMO)));
    }

    @PostMapping("/vnpay/initiate")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiateVNPay(@Valid @RequestBody VNPayPaymentRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success(paymentService.processPayment(request.getOrderId(), PaymentMethod.VNPAY)));
    }

    @GetMapping("/order/{orderId}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getStatus(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentStatus(orderId)));
    }

    /**
     * Webhook for Momo. In a real application, this should be public but validated
     * with signature.
     */
    @PostMapping("/momo/callback")
    public ResponseEntity<String> momoCallback(@RequestBody Map<String, String> payload) {
        log.info("Received Momo callback: {}", payload);
        // Signature validation and transaction update logic here
        return ResponseEntity.ok("Success");
    }

    /**
     * IPN for VNPay.
     */
    @GetMapping("/vnpay/callback")
    public ResponseEntity<Map<String, String>> vnpayCallback(@RequestParam Map<String, String> allParams) {
        log.info("Received VNPay callback: {}", allParams);
        // Signature validation and transaction update logic here
        return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
    }
}
