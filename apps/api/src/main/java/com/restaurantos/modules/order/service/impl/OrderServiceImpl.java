package com.restaurantos.modules.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.menu.entity.MenuItem;
import com.restaurantos.modules.menu.repository.MenuItemRepository;
import com.restaurantos.modules.order.dto.request.CreateOrderRequest;
import com.restaurantos.modules.order.dto.request.UpdateOrderStatusRequest;
import com.restaurantos.modules.order.dto.response.OrderResponse;
import com.restaurantos.modules.order.entity.Order;
import com.restaurantos.modules.order.entity.OrderItem;
import com.restaurantos.modules.order.entity.OrderItemStatus;
import com.restaurantos.modules.order.entity.OrderStatus;
import com.restaurantos.modules.order.entity.PaymentStatus;
import com.restaurantos.modules.order.event.OrderCompletedEvent;
import com.restaurantos.modules.order.repository.OrderRepository;
import com.restaurantos.modules.order.service.OrderService;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.modules.table.entity.RestaurantTable;
import com.restaurantos.modules.table.repository.TableRepository;
import com.restaurantos.shared.exception.ResourceNotFoundException;
import com.restaurantos.shared.websocket.WebSocketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final TableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final WebSocketService webSocketService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll(UUID restaurantId, OrderStatus status) {
        List<Order> orders;
        if (status != null) {
            orders = orderRepository.findByRestaurantIdAndStatusOrderByCreatedAtDesc(restaurantId, status);
        } else {
            orders = orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
        }
        return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(UUID id) {
        return orderRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    @Transactional
    public OrderResponse create(UUID restaurantId, CreateOrderRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        RestaurantTable table = null;
        if (request.getTableId() != null) {
            table = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
        }

        String orderNumber = generateOrderNumber(restaurantId);

        Order order = Order.builder()
                .restaurant(restaurant)
                .table(table)
                .orderNumber(orderNumber)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .specialInstructions(request.getSpecialInstructions())
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .subtotal(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Menu item not found: " + itemReq.getMenuItemId()));

            BigDecimal itemSubtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .itemName(menuItem.getName())
                    .unitPrice(menuItem.getPrice())
                    .quantity(itemReq.getQuantity())
                    .subtotal(itemSubtotal)
                    .specialInstructions(itemReq.getSpecialInstructions())
                    .status(OrderItemStatus.PENDING)
                    .build();

            order.addItem(orderItem);
            subtotal = subtotal.add(itemSubtotal);
        }

        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal.add(order.getTaxAmount()).subtract(order.getDiscountAmount()));

        Order savedOrder = orderRepository.save(order);
        OrderResponse response = mapToResponse(savedOrder);

        // Broadcast to kitchen and dashboard
        webSocketService.broadcastToKitchen(restaurantId, response);
        webSocketService.broadcastToDashboard(restaurantId, response);

        return response;
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(UUID id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        if (oldStatus == newStatus)
            return mapToResponse(order);

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.CONFIRMED) {
            order.setConfirmedAt(LocalDateTime.now());
        } else if (newStatus == OrderStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setPaidAt(LocalDateTime.now());

            // Publish event for inventory deduction
            eventPublisher.publishEvent(new OrderCompletedEvent(order.getId(), order.getRestaurant().getId()));
        } else if (newStatus == OrderStatus.CANCELLED) {
            order.setCancelledAt(LocalDateTime.now());
        }

        Order savedOrder = orderRepository.save(order);
        OrderResponse response = mapToResponse(savedOrder);

        // Broadcast status update
        webSocketService.broadcastToOrder(order.getId(), response);
        webSocketService.broadcastToKitchen(order.getRestaurant().getId(), response);
        webSocketService.broadcastToDashboard(order.getRestaurant().getId(), response);

        return response;
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID id, String reason) {
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .status(OrderStatus.CANCELLED)
                .build();
        // Reason could be logged or stored in a separate Audit table
        return updateStatus(id, request);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found");
        }
        orderRepository.deleteById(id);
    }

    private String generateOrderNumber(UUID restaurantId) {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long count = orderRepository.countByRestaurantIdAndCreatedAtAfter(restaurantId, startOfDay);
        return LocalDate.now().format(DATE_FORMATTER) + String.format("%04d", count + 1);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .tableId(order.getTable() != null ? order.getTable().getId() : null)
                .tableNumber(order.getTable() != null ? order.getTable().getTableNumber().toString() : "N/A")
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .paidAt(order.getPaidAt())
                .specialInstructions(order.getSpecialInstructions())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(this::mapItemToResponse).collect(Collectors.toList()))
                .build();
    }

    private OrderResponse.OrderItemResponse mapItemToResponse(OrderItem item) {
        return OrderResponse.OrderItemResponse.builder()
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
