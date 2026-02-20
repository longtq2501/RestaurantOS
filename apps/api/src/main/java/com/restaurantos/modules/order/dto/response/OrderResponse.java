package com.restaurantos.modules.order.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.restaurantos.modules.order.entity.OrderItemStatus;
import com.restaurantos.modules.order.entity.OrderStatus;
import com.restaurantos.modules.order.entity.PaymentStatus;
import com.restaurantos.modules.payment.entity.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for order details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String orderNumber;
    private UUID tableId;
    private String tableNumber;
    private String customerName;
    private String customerPhone;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;
    private String specialInstructions;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID id;
        private UUID menuItemId;
        private String itemName;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;
        private OrderItemStatus status;
        private String specialInstructions;
    }
}
