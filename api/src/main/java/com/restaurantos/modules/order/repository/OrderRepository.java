package com.restaurantos.modules.order.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.order.entity.Order;
import com.restaurantos.modules.order.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByRestaurantIdAndStatusOrderByCreatedAtDesc(UUID restaurantId, OrderStatus status);

    List<Order> findByRestaurantIdAndCreatedAtBetween(UUID restaurantId, LocalDateTime start, LocalDateTime end);

    List<Order> findByRestaurantIdOrderByCreatedAtDesc(UUID restaurantId);

    // For auto-incrementing order number per day
    long countByRestaurantIdAndCreatedAtAfter(UUID restaurantId, LocalDateTime startOfDay);
}
