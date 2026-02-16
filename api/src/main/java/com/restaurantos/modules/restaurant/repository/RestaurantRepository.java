package com.restaurantos.modules.restaurant.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.restaurant.entity.Restaurant;

/**
 * Repository interface for {@link Restaurant} entity.
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    /**
     * Finds a restaurant by its unique slug.
     *
     * @param slug the url-friendly name of the restaurant
     * @return an Optional containing the restaurant if found, or empty otherwise
     */
    Optional<Restaurant> findBySlug(String slug);

    /**
     * Checks if a restaurant exists with the given slug.
     *
     * @param slug the slug to check
     * @return true if it exists, false otherwise
     */
    Boolean existsBySlug(String slug);
}
