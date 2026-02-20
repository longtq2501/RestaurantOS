package com.restaurantos.modules.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.restaurantos.modules.order.entity.Order;
import com.restaurantos.modules.order.repository.OrderRepository;
import com.restaurantos.modules.payment.entity.Transaction;
import com.restaurantos.modules.payment.entity.TransactionStatus;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.shared.config.JpaConfig;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        Restaurant restaurant = Restaurant.builder()
                .name("Payment Resto")
                .slug("payment-resto")
                .build();
        restaurant = restaurantRepository.save(restaurant);

        order = Order.builder()
                .restaurant(restaurant)
                .orderNumber("2602167701")
                .totalAmount(BigDecimal.valueOf(50000))
                .paymentStatus(com.restaurantos.modules.order.entity.PaymentStatus.UNPAID)
                .build();
        order = orderRepository.save(order);
    }

    @Test
    void findByGatewayTransactionId_ShouldReturnTransaction() {
        // Given
        Transaction transaction = Transaction.builder()
                .order(order)
                .amount(BigDecimal.valueOf(50000))
                .paymentMethod(com.restaurantos.modules.payment.entity.PaymentMethod.MOMO)
                .gatewayTransactionId("MOMO123456")
                .status(TransactionStatus.COMPLETED)
                .build();
        transactionRepository.save(transaction);

        // When
        Optional<Transaction> found = transactionRepository.findByGatewayTransactionId("MOMO123456");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50000));
    }
}
