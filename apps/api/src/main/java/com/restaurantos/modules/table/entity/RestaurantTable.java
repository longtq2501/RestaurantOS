package com.restaurantos.modules.table.entity;

import java.util.UUID;

import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.shared.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a physical table in a restaurant.
 */
@Entity
@Table(name = "restaurant_tables", uniqueConstraints = {
                @UniqueConstraint(name = "uk_table_restaurant_number", columnNames = { "restaurant_id",
                                "table_number" })
}, indexes = {
                @Index(name = "idx_table_restaurant", columnList = "restaurant_id"),
                @Index(name = "idx_table_qr_token", columnList = "qr_code_token")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "restaurant" })
public class RestaurantTable extends BaseEntity {

        @Column(name = "table_number", nullable = false)
        private Integer tableNumber;

        @Column(nullable = false)
        private Integer capacity;

        @Column(name = "qr_code_token", unique = true)
        private String qrCodeToken;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 30)
        @Builder.Default
        private TableStatus status = TableStatus.EMPTY;

        @Column(length = 50)
        private String section;

        @Column(name = "current_order_id")
        private UUID currentOrderId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "restaurant_id", nullable = false)
        private Restaurant restaurant;
}
