package com.restaurantos.modules.inventory.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurantos.modules.inventory.dto.request.IngredientRequest;
import com.restaurantos.modules.inventory.dto.request.StockAdjustmentRequest;
import com.restaurantos.modules.inventory.dto.response.IngredientResponse;
import com.restaurantos.modules.inventory.service.IngredientService;
import com.restaurantos.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing ingredients.
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> getAll(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.getAllByRestaurant(restaurantId)));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'KITCHEN')")
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> getLowStock(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.getLowStockItems(restaurantId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<IngredientResponse>> create(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody IngredientRequest request) {
        IngredientResponse response = ingredientService.create(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ingredient created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<IngredientResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody IngredientRequest request) {
        IngredientResponse response = ingredientService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Ingredient updated successfully", response));
    }

    @PostMapping("/{id}/adjust")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'KITCHEN')")
    public ResponseEntity<ApiResponse<IngredientResponse>> adjustStock(
            @PathVariable UUID id,
            @Valid @RequestBody StockAdjustmentRequest request) {
        IngredientResponse response = ingredientService.adjustStock(id, request);
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        ingredientService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Ingredient deleted successfully", null));
    }
}
