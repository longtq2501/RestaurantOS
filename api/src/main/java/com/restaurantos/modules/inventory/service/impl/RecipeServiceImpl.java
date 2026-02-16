package com.restaurantos.modules.inventory.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.inventory.dto.request.RecipeRequest;
import com.restaurantos.modules.inventory.dto.response.RecipeResponse;
import com.restaurantos.modules.inventory.entity.Ingredient;
import com.restaurantos.modules.inventory.entity.RecipeIngredient;
import com.restaurantos.modules.inventory.entity.RecipeIngredientId;
import com.restaurantos.modules.inventory.repository.IngredientRepository;
import com.restaurantos.modules.inventory.repository.RecipeIngredientRepository;
import com.restaurantos.modules.inventory.service.RecipeService;
import com.restaurantos.modules.menu.entity.MenuItem;
import com.restaurantos.modules.menu.repository.MenuItemRepository;
import com.restaurantos.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeIngredientRepository recipeRepository;
    private final MenuItemRepository menuItemRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeByMenuItem(UUID menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + menuItemId));

        List<RecipeIngredient> ingredients = recipeRepository.findByMenuItemId(menuItemId);

        return RecipeResponse.builder()
                .menuItemId(menuItem.getId())
                .menuItemName(menuItem.getName())
                .ingredients(ingredients.stream()
                        .map(ri -> RecipeResponse.RecipeIngredientResponse.builder()
                                .ingredientId(ri.getIngredient().getId())
                                .ingredientName(ri.getIngredient().getName())
                                .quantity(ri.getQuantity())
                                .unit(ri.getIngredient().getUnit())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public RecipeResponse saveRecipe(UUID menuItemId, RecipeRequest request) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + menuItemId));

        // Remove existing recipe if any
        recipeRepository.deleteByMenuItemId(menuItemId);

        List<RecipeIngredient> newIngredients = request.getIngredients().stream()
                .map(req -> {
                    Ingredient ingredient = ingredientRepository.findById(req.getIngredientId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Ingredient not found with id: " + req.getIngredientId()));

                    return RecipeIngredient.builder()
                            .id(new RecipeIngredientId(menuItemId, ingredient.getId()))
                            .menuItem(menuItem)
                            .ingredient(ingredient)
                            .quantity(req.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        recipeRepository.saveAll(newIngredients);

        return getRecipeByMenuItem(menuItemId);
    }

    @Override
    @Transactional
    public void deleteRecipe(UUID menuItemId) {
        if (!menuItemRepository.existsById(menuItemId)) {
            throw new ResourceNotFoundException("Menu item not found with id: " + menuItemId);
        }
        recipeRepository.deleteByMenuItemId(menuItemId);
    }
}
