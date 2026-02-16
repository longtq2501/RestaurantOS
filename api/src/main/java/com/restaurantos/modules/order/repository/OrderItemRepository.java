package com.restaurantos.modules.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.order.entity.OrderItem;
import com.restaurantos.modules.order.entity.OrderItemStatus;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrderId(UUID orderId);

    List<OrderItem> findByOrderIdAndStatus(UUID orderId, OrderItemStatus status);
}
