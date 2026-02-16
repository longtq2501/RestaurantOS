package com.restaurantos.modules.inventory.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.inventory.dto.request.IngredientRequest;
import com.restaurantos.modules.inventory.dto.request.StockAdjustmentRequest;
import com.restaurantos.modules.inventory.dto.response.IngredientResponse;
import com.restaurantos.modules.inventory.entity.Ingredient;
import com.restaurantos.modules.inventory.entity.InventoryHistory;
import com.restaurantos.modules.inventory.repository.IngredientRepository;
import com.restaurantos.modules.inventory.repository.InventoryHistoryRepository;
import com.restaurantos.modules.inventory.service.IngredientService;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final InventoryHistoryRepository historyRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional(readOnly = true)
    public List<IngredientResponse> getAllByRestaurant(UUID restaurantId) {
        return ingredientRepository.findByRestaurantIdOrderByNameAsc(restaurantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientResponse> getLowStockItems(UUID restaurantId) {
        return ingredientRepository.findByRestaurantIdAndCurrentStockLessThanMinStock(restaurantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IngredientResponse getById(UUID id) {
        return ingredientRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));
    }

    @Override
    @Transactional
    public IngredientResponse create(UUID restaurantId, IngredientRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .currentStock(request.getCurrentStock())
                .minStock(request.getMinStock())
                .costPerUnit(request.getCostPerUnit())
                .supplierName(request.getSupplierName())
                .supplierPhone(request.getSupplierPhone())
                .restaurant(restaurant)
                .build();

        return mapToResponse(ingredientRepository.save(ingredient));
    }

    @Override
    @Transactional
    public IngredientResponse update(UUID id, IngredientRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));

        ingredient.setName(request.getName());
        ingredient.setUnit(request.getUnit());
        ingredient.setMinStock(request.getMinStock());
        ingredient.setCostPerUnit(request.getCostPerUnit());
        ingredient.setSupplierName(request.getSupplierName());
        ingredient.setSupplierPhone(request.getSupplierPhone());
        // Standard update doesn't move currentStock to force use of adjustStock for
        // tracking

        return mapToResponse(ingredientRepository.save(ingredient));
    }

    @Override
    @Transactional
    public IngredientResponse adjustStock(UUID id, StockAdjustmentRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));

        ingredient.setCurrentStock(ingredient.getCurrentStock().add(request.getQuantity()));

        InventoryHistory history = InventoryHistory.builder()
                .ingredient(ingredient)
                .usageQuantity(request.getQuantity())
                .adjustmentType(request.getType())
                .reason(request.getReason())
                .build();

        historyRepository.save(history);
        return mapToResponse(ingredientRepository.save(ingredient));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!ingredientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
    }

    private IngredientResponse mapToResponse(Ingredient ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .unit(ingredient.getUnit())
                .currentStock(ingredient.getCurrentStock())
                .minStock(ingredient.getMinStock())
                .costPerUnit(ingredient.getCostPerUnit())
                .supplierName(ingredient.getSupplierName())
                .supplierPhone(ingredient.getSupplierPhone())
                .createdAt(ingredient.getCreatedAt())
                .updatedAt(ingredient.getUpdatedAt())
                .build();
    }
}
