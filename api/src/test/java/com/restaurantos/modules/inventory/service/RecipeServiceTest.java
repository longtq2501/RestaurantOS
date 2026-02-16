package com.restaurantos.modules.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurantos.modules.inventory.dto.request.RecipeRequest;
import com.restaurantos.modules.inventory.dto.response.RecipeResponse;
import com.restaurantos.modules.inventory.entity.Ingredient;
import com.restaurantos.modules.inventory.entity.RecipeIngredient;
import com.restaurantos.modules.inventory.repository.IngredientRepository;
import com.restaurantos.modules.inventory.repository.RecipeIngredientRepository;
import com.restaurantos.modules.inventory.service.impl.RecipeServiceImpl;
import com.restaurantos.modules.menu.entity.MenuItem;
import com.restaurantos.modules.menu.repository.MenuItemRepository;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeIngredientRepository recipeRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    private MenuItem menuItem;
    private UUID menuItemId;

    @BeforeEach
    void setUp() {
        menuItemId = UUID.randomUUID();
        menuItem = MenuItem.builder()
                .name("Pho")
                .build();
        menuItem.setId(menuItemId);
    }

    @Test
    void getRecipeByMenuItem_ShouldReturnRecipe() {
        // Given
        Ingredient ingredient = Ingredient.builder()
                .name("Noodles")
                .unit("g")
                .build();
        ingredient.setId(UUID.randomUUID());

        RecipeIngredient ri = RecipeIngredient.builder()
                .menuItem(menuItem)
                .ingredient(ingredient)
                .quantity(BigDecimal.valueOf(200))
                .build();

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(recipeRepository.findByMenuItemId(menuItemId)).thenReturn(Collections.singletonList(ri));

        // When
        RecipeResponse response = recipeService.getRecipeByMenuItem(menuItemId);

        // Then
        assertThat(response.getMenuItemName()).isEqualTo("Pho");
        assertThat(response.getIngredients()).hasSize(1);
        assertThat(response.getIngredients().get(0).getIngredientName()).isEqualTo("Noodles");
    }

    @Test
    void saveRecipe_ShouldDeleteOldAndSaveNew() {
        // Given
        UUID ingredientId = UUID.randomUUID();
        Ingredient ingredient = Ingredient.builder()
                .name("Noodles")
                .build();
        ingredient.setId(ingredientId);

        RecipeRequest request = RecipeRequest.builder()
                .ingredients(Collections.singletonList(
                        RecipeRequest.RecipeIngredientRequest.builder()
                                .ingredientId(ingredientId)
                                .quantity(BigDecimal.valueOf(200))
                                .build()))
                .build();

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));

        // When
        recipeService.saveRecipe(menuItemId, request);

        // Then
        verify(recipeRepository).deleteByMenuItemId(menuItemId);
        verify(recipeRepository).saveAll(any(List.class));
    }
}
