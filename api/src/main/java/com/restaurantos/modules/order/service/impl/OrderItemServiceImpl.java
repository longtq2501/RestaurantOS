package com.restaurantos.modules.order.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.order.dto.request.UpdateOrderItemStatusRequest;
import com.restaurantos.modules.order.dto.response.OrderResponse.OrderItemResponse;
import com.restaurantos.modules.order.entity.OrderItem;
import com.restaurantos.modules.order.entity.OrderItemStatus;
import com.restaurantos.modules.order.repository.OrderItemRepository;
import com.restaurantos.modules.order.service.OrderItemService;
import com.restaurantos.shared.exception.ResourceNotFoundException;
import com.restaurantos.shared.websocket.WebSocketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final WebSocketService webSocketService;

    @Override
    @Transactional
    public OrderItemResponse updateStatus(UUID id, UpdateOrderItemStatusRequest request) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found: " + id));

        OrderItemStatus newStatus = request.getStatus();

        if (item.getStatus() == newStatus)
            return mapToResponse(item);

        item.setStatus(newStatus);

        if (newStatus == OrderItemStatus.PREPARING) {
            item.setStartedPreparingAt(LocalDateTime.now());
        } else if (newStatus == OrderItemStatus.READY) {
            item.setReadyAt(LocalDateTime.now());
        } else if (newStatus == OrderItemStatus.SERVED) {
            item.setServedAt(LocalDateTime.now());
        }

        OrderItem savedItem = orderItemRepository.save(item);
        OrderItemResponse response = mapToResponse(savedItem);

        // Broadcast to order, kitchen and dashboard
        UUID restaurantId = item.getOrder().getRestaurant().getId();
        webSocketService.broadcastToOrder(item.getOrder().getId(), response);
        webSocketService.broadcastToKitchen(restaurantId, response);
        webSocketService.broadcastToDashboard(restaurantId, response);

        return response;
    }

    private OrderItemResponse mapToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem() != null ? item.getMenuItem().getId() : null)
                .itemName(item.getItemName())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .status(item.getStatus())
                .specialInstructions(item.getSpecialInstructions())
                .build();
    }
}
