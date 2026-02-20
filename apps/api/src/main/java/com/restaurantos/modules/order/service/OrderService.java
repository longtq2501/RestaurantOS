package com.restaurantos.modules.order.service;

import java.util.List;
import java.util.UUID;

import com.restaurantos.modules.order.dto.request.CreateOrderRequest;
import com.restaurantos.modules.order.dto.request.UpdateOrderStatusRequest;
import com.restaurantos.modules.order.dto.response.OrderResponse;
import com.restaurantos.modules.order.entity.OrderStatus;

/**
 * Service interface for order management.
 */
public interface OrderService {

    List<OrderResponse> getAll(UUID restaurantId, OrderStatus status);

    OrderResponse getById(UUID id);

    OrderResponse create(UUID restaurantId, CreateOrderRequest request);

    OrderResponse updateStatus(UUID id, UpdateOrderStatusRequest request);

    OrderResponse cancel(UUID id, String reason);

    void delete(UUID id);
}
