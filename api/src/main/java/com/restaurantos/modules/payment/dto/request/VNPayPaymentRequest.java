package com.restaurantos.modules.payment.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VNPayPaymentRequest {
    @NotNull
    private UUID orderId;
}
