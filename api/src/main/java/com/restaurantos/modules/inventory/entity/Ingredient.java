package com.restaurantos.modules.inventory.entity;

import java.math.BigDecimal;

import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.shared.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing an ingredient in the inventory.
 */
@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "restaurant")
public class Ingredient extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String unit; // e.g., kg, gram, ml, unit

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal minStock = BigDecimal.ZERO;

    private BigDecimal costPerUnit;

    private String supplierName;

    private String supplierPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
}
