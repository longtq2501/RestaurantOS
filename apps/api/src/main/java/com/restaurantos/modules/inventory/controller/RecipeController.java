package com.restaurantos.modules.inventory.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurantos.modules.inventory.dto.request.RecipeRequest;
import com.restaurantos.modules.inventory.dto.response.RecipeResponse;
import com.restaurantos.modules.inventory.service.RecipeService;
import com.restaurantos.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing item recipes.
 */
@RestController
@RequestMapping("/api/menu-items/{menuItemId}/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<ApiResponse<RecipeResponse>> getRecipe(@PathVariable UUID menuItemId) {
        return ResponseEntity.ok(ApiResponse.success(recipeService.getRecipeByMenuItem(menuItemId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<RecipeResponse>> saveRecipe(
            @PathVariable UUID menuItemId,
            @Valid @RequestBody RecipeRequest request) {
        RecipeResponse response = recipeService.saveRecipe(menuItemId, request);
        return ResponseEntity.ok(ApiResponse.success("Recipe saved successfully", response));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> deleteRecipe(@PathVariable UUID menuItemId) {
        recipeService.deleteRecipe(menuItemId);
        return ResponseEntity.ok(ApiResponse.success("Recipe deleted successfully", null));
    }
}
