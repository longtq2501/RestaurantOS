package com.restaurantos.modules.inventory.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.inventory.entity.RecipeIngredient;
import com.restaurantos.modules.inventory.entity.RecipeIngredientId;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientId> {

    List<RecipeIngredient> findByMenuItemId(UUID menuItemId);

    void deleteByMenuItemId(UUID menuItemId);
}
