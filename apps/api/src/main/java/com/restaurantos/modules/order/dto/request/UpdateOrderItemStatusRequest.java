package com.restaurantos.modules.order.dto.request;

import com.restaurantos.modules.order.entity.OrderItemStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating order item status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderItemStatusRequest {
    @NotNull(message = "Status is required")
    private OrderItemStatus status;
}
