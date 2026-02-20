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

    @org.springframework.data.jpa.repository.Query("SELECT new com.restaurantos.modules.analytics.dto.response.TopDishResponse("
            +
            "oi.menuItem.id, oi.itemName, SUM(oi.quantity), SUM(oi.subtotal)) " +
            "FROM OrderItem oi " +
            "WHERE oi.order.restaurant.id = :restaurantId " +
            "AND oi.order.createdAt >= :startDate " +
            "AND oi.order.createdAt <= :endDate " +
            "AND oi.order.status = 'COMPLETED' " +
            "GROUP BY oi.menuItem.id, oi.itemName " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<com.restaurantos.modules.analytics.dto.response.TopDishResponse> findTopSellingItems(
            @org.springframework.data.repository.query.Param("restaurantId") UUID restaurantId,
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate,
            org.springframework.data.domain.Pageable pageable);
}
