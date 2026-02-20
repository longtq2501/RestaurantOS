package com.restaurantos.modules.menu.entity;

import java.math.BigDecimal;

import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.shared.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
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
 * Entity representing an individual menu item.
 */
@Entity
@Table(name = "menu_items", indexes = {
        @Index(name = "idx_menu_item_restaurant_id", columnList = "restaurant_id"),
        @Index(name = "idx_menu_item_category_id", columnList = "category_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "restaurant", "category" })
public class MenuItem extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String thumbnailUrl;

    private Integer prepTime; // in minutes

    private Integer spicyLevel; // e.g., 0-3

    @Column(nullable = false)
    @Builder.Default
    private java.lang.Boolean isVegetarian = false;

    @Column(length = 500)
    private String allergens;

    @Column(nullable = false)
    @Builder.Default
    private java.lang.Boolean isAvailable = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private java.lang.Boolean isFeatured = false;

    @Column(nullable = false)
    @Builder.Default
    private java.lang.Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private Long orderCount = 0L;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal ratingAvg = BigDecimal.valueOf(0.0);

    @Column(nullable = false)
    @Builder.Default
    private Long ratingCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private MenuCategory category;
}
