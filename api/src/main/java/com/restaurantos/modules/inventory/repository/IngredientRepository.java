package com.restaurantos.modules.inventory.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.inventory.entity.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {

    List<Ingredient> findByRestaurantIdOrderByNameAsc(UUID restaurantId);

    @Query("SELECT i FROM Ingredient i WHERE i.restaurant.id = :restaurantId AND i.currentStock < i.minStock")
    List<Ingredient> findByRestaurantIdAndCurrentStockLessThanMinStock(UUID restaurantId);
}
