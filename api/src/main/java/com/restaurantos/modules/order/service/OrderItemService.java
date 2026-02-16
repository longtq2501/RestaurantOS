package com.restaurantos.modules.order.service;

import java.util.UUID;

import com.restaurantos.modules.order.dto.request.UpdateOrderItemStatusRequest;
import com.restaurantos.modules.order.dto.response.OrderResponse.OrderItemResponse;

/**
 * Service interface for order item preparation tracking.
 */
public interface OrderItemService {

    OrderItemResponse updateStatus(UUID id, UpdateOrderItemStatusRequest request);
}
