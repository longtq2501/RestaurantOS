package com.restaurantos.modules.order.dto.request;

import java.util.List;
import java.util.UUID;

import com.restaurantos.modules.order.entity.PaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    private UUID tableId;

    private String customerName;

    private String customerPhone;

    private String specialInstructions;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Menu item ID is required")
        private UUID menuItemId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private String specialInstructions;
    }
}
