package com.restaurantos.modules.menu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.menu.entity.MenuCategory;

/**
 * Repository interface for {@link MenuCategory} entity.
 */
@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {

    /**
     * Finds active categories for a specific restaurant ordered by display order.
     *
     * @param restaurantId the restaurant ID
     * @return list of active menu categories
     */
    List<MenuCategory> findByRestaurantIdAndIsActiveTrueOrderByDisplayOrderAsc(UUID restaurantId);

    /**
     * Finds all categories for a specific restaurant ordered by display order.
     *
     * @param restaurantId the restaurant ID
     * @return list of menu categories
     */
    List<MenuCategory> findByRestaurantIdOrderByDisplayOrderAsc(UUID restaurantId);
}
