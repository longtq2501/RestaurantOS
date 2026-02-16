package com.restaurantos.modules.menu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.menu.entity.MenuItem;

/**
 * Repository interface for {@link MenuItem} entity.
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

    /**
     * Finds all menu items for a specific restaurant.
     *
     * @param restaurantId the restaurant ID
     * @return list of menu items
     */
    List<MenuItem> findByRestaurantId(UUID restaurantId);

    /**
     * Finds all menu items for a specific category.
     *
     * @param categoryId the category ID
     * @return list of menu items
     */
    List<MenuItem> findByCategoryId(UUID categoryId);

    /**
     * Finds active menu items for a specific restaurant.
     *
     * @param restaurantId the restaurant ID
     * @return list of available menu items
     */
    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(UUID restaurantId);

    /**
     * Searches for menu items by name or description using partial matching.
     * Note: Full-text search index is defined at database level.
     *
     * @param restaurantId the restaurant ID
     * @param query        the search query
     * @return list of matching menu items
     */
    @Query("SELECT mi FROM MenuItem mi WHERE mi.restaurant.id = :restaurantId " +
            "AND (LOWER(mi.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(mi.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<MenuItem> search(@Param("restaurantId") UUID restaurantId, @Param("query") String query);
}
