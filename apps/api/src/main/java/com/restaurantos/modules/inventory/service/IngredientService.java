package com.restaurantos.modules.inventory.service;

import java.util.List;
import java.util.UUID;

import com.restaurantos.modules.inventory.dto.request.IngredientRequest;
import com.restaurantos.modules.inventory.dto.request.StockAdjustmentRequest;
import com.restaurantos.modules.inventory.dto.response.IngredientResponse;

/**
 * Service interface for ingredient management.
 */
public interface IngredientService {

    List<IngredientResponse> getAllByRestaurant(UUID restaurantId);

    List<IngredientResponse> getLowStockItems(UUID restaurantId);

    IngredientResponse getById(UUID id);

    IngredientResponse create(UUID restaurantId, IngredientRequest request);

    IngredientResponse update(UUID id, IngredientRequest request);

    IngredientResponse adjustStock(UUID id, StockAdjustmentRequest request);

    void delete(UUID id);
}
