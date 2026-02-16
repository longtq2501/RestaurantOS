package com.restaurantos.modules.payment.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MomoPaymentRequest {
    @NotNull
    private UUID orderId;
}
