package com.restaurantos.modules.order.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurantos.modules.order.dto.request.CreateOrderRequest;
import com.restaurantos.modules.order.dto.request.UpdateOrderItemStatusRequest;
import com.restaurantos.modules.order.dto.request.UpdateOrderStatusRequest;
import com.restaurantos.modules.order.dto.response.OrderResponse;
import com.restaurantos.modules.order.dto.response.OrderResponse.OrderItemResponse;
import com.restaurantos.modules.order.entity.OrderStatus;
import com.restaurantos.modules.order.service.OrderItemService;
import com.restaurantos.modules.order.service.OrderService;
import com.restaurantos.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing orders.
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'STAFF', 'KITCHEN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAll(
            @PathVariable UUID restaurantId,
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAll(restaurantId, status)));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'STAFF', 'KITCHEN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getById(id)));
    }

    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.create(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", response));
    }

    @PutMapping("/orders/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'STAFF', 'KITCHEN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", response));
    }

    @PutMapping("/order-items/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'STAFF', 'KITCHEN')")
    public ResponseEntity<ApiResponse<OrderItemResponse>> updateItemStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderItemStatusRequest request) {
        OrderItemResponse response = orderItemService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Order item status updated", response));
    }

    @DeleteMapping("/orders/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        orderService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Order deleted", null));
    }
}
