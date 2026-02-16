package com.restaurantos.modules.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.restaurantos.modules.menu.entity.MenuItem;
import com.restaurantos.modules.menu.repository.MenuItemRepository;
import com.restaurantos.modules.order.dto.request.CreateOrderRequest;
import com.restaurantos.modules.order.dto.request.UpdateOrderStatusRequest;
import com.restaurantos.modules.order.dto.response.OrderResponse;
import com.restaurantos.modules.order.entity.Order;
import com.restaurantos.modules.order.entity.OrderStatus;
import com.restaurantos.modules.order.event.OrderCompletedEvent;
import com.restaurantos.modules.order.repository.OrderRepository;
import com.restaurantos.modules.order.service.impl.OrderServiceImpl;
import com.restaurantos.modules.payment.entity.PaymentMethod;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.modules.table.entity.RestaurantTable;
import com.restaurantos.modules.table.repository.TableRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

        @Mock
        private OrderRepository orderRepository;

        @Mock
        private RestaurantRepository restaurantRepository;

        @Mock
        private TableRepository tableRepository;

        @Mock
        private MenuItemRepository menuItemRepository;

        @Mock
        private ApplicationEventPublisher eventPublisher;

        @InjectMocks
        private OrderServiceImpl orderService;

        private Restaurant restaurant;
        private UUID restaurantId;

        @BeforeEach
        void setUp() {
                restaurantId = UUID.randomUUID();
                restaurant = Restaurant.builder()
                                .name("Order Resto")
                                .slug("order-resto")
                                .build();
                restaurant.setId(restaurantId);
        }

        @Test
        void create_ShouldReturnSavedOrder() {
                // Given
                UUID menuItemId = UUID.randomUUID();
                MenuItem menuItem = MenuItem.builder()
                                .name("Burger")
                                .price(BigDecimal.valueOf(100000))
                                .build();
                menuItem.setId(menuItemId);

                UUID tableId = UUID.randomUUID();
                RestaurantTable table = RestaurantTable.builder()
                                .tableNumber(1)
                                .build();
                table.setId(tableId);

                CreateOrderRequest request = CreateOrderRequest.builder()
                                .tableId(tableId)
                                .customerName("John Doe")
                                .paymentMethod(PaymentMethod.CASH)
                                .items(Collections.singletonList(
                                                CreateOrderRequest.OrderItemRequest.builder()
                                                                .menuItemId(menuItemId)
                                                                .quantity(2)
                                                                .build()))
                                .build();

                when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
                when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
                when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
                when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                        Order o = invocation.getArgument(0);
                        o.setId(UUID.randomUUID());
                        return o;
                });

                // When
                OrderResponse response = orderService.create(restaurantId, request);

                // Then
                assertThat(response.getOrderNumber()).isNotEmpty();
                assertThat(response.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(200000));
                assertThat(response.getItems()).hasSize(1);
                verify(orderRepository).save(any(Order.class));
        }

        @Test
        void updateStatus_ToCompleted_ShouldPublishEvent() {
                // Given
                UUID orderId = UUID.randomUUID();
                Order order = Order.builder()
                                .restaurant(restaurant)
                                .status(OrderStatus.CONFIRMED)
                                .build();
                order.setId(orderId);

                UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                                .status(OrderStatus.COMPLETED)
                                .build();

                when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
                when(orderRepository.save(any(Order.class))).thenReturn(order);

                // When
                orderService.updateStatus(orderId, request);

                // Then
                assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                verify(eventPublisher).publishEvent(any(OrderCompletedEvent.class));
        }
}
