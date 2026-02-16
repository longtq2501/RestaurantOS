package com.restaurantos.modules.inventory.service;

import java.util.UUID;

import com.restaurantos.modules.inventory.dto.request.RecipeRequest;
import com.restaurantos.modules.inventory.dto.response.RecipeResponse;

/**
 * Service interface for recipe management.
 */
public interface RecipeService {

    RecipeResponse getRecipeByMenuItem(UUID menuItemId);

    RecipeResponse saveRecipe(UUID menuItemId, RecipeRequest request);

    void deleteRecipe(UUID menuItemId);
}
