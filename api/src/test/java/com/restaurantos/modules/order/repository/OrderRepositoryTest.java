package com.restaurantos.modules.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.restaurantos.modules.order.entity.Order;
import com.restaurantos.modules.order.entity.OrderStatus;
import com.restaurantos.modules.payment.entity.PaymentMethod;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.shared.config.JpaConfig;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
class OrderRepositoryTest {

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private RestaurantRepository restaurantRepository;

        private Restaurant restaurant;

        @BeforeEach
        void setUp() {
                restaurant = Restaurant.builder()
                                .name("Order Resto")
                                .slug("order-resto")
                                .address("123 Order St")
                                .phone("0123456789")
                                .build();
                restaurant = restaurantRepository.save(restaurant);
        }

        @Test
        void findByRestaurantIdAndStatus_ShouldReturnCorrectOrders() {
                // Given
                Order order1 = Order.builder()
                                .restaurant(restaurant)
                                .orderNumber("2602160001")
                                .status(OrderStatus.PENDING)
                                .subtotal(BigDecimal.TEN)
                                .totalAmount(BigDecimal.TEN)
                                .paymentMethod(PaymentMethod.CASH)
                                .build();

                Order order2 = Order.builder()
                                .restaurant(restaurant)
                                .orderNumber("2602160002")
                                .status(OrderStatus.COMPLETED)
                                .subtotal(BigDecimal.TEN)
                                .totalAmount(BigDecimal.TEN)
                                .paymentMethod(PaymentMethod.CASH)
                                .build();

                orderRepository.save(order1);
                orderRepository.save(order2);

                // When
                List<Order> pendingOrders = orderRepository.findByRestaurantIdAndStatusOrderByCreatedAtDesc(
                                restaurant.getId(),
                                OrderStatus.PENDING);

                // Then
                assertThat(pendingOrders).hasSize(1);
                assertThat(pendingOrders.get(0).getOrderNumber()).isEqualTo("2602160001");
        }

        @Test
        void countByRestaurantIdAndCreatedAtAfter_ShouldReturnDailyCount() {
                // Given
                LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

                Order order = Order.builder()
                                .restaurant(restaurant)
                                .orderNumber("2602160001")
                                .status(OrderStatus.PENDING)
                                .subtotal(BigDecimal.TEN)
                                .totalAmount(BigDecimal.TEN)
                                .paymentMethod(PaymentMethod.CASH)
                                .build();
                orderRepository.save(order);

                // When
                long count = orderRepository.countByRestaurantIdAndCreatedAtAfter(restaurant.getId(), startOfDay);

                // Then
                assertThat(count).isEqualTo(1);
        }
}
