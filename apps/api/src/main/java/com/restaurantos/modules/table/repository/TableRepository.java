package com.restaurantos.modules.table.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.table.entity.RestaurantTable;

/**
 * Data access layer for {@link RestaurantTable} entity.
 */
@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, UUID> {

    /**
     * Finds all tables for a specific restaurant, ordered by their table number.
     *
     * @param restaurantId ID of the restaurant.
     * @return List of tables.
     */
    List<RestaurantTable> findByRestaurantIdOrderByTableNumberAsc(UUID restaurantId);

    /**
     * Finds a table by its unique QR code token.
     *
     * @param qrCodeToken The token embedded in the QR code.
     * @return Optional containing the found table, or empty if not found.
     */
    Optional<RestaurantTable> findByQrCodeToken(String qrCodeToken);

    /**
     * Checks if a table number already exists within a specific restaurant.
     *
     * @param restaurantId ID of the restaurant.
     * @param tableNumber  Table display number to check.
     * @return true if exists, false otherwise.
     */
    boolean existsByRestaurantIdAndTableNumber(UUID restaurantId, Integer tableNumber);
}
