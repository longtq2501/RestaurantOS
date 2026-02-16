package com.restaurantos.modules.inventory.service.impl;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.inventory.repository.RecipeIngredientRepository;
import com.restaurantos.modules.inventory.service.IngredientService;
import com.restaurantos.modules.inventory.service.InventoryDeductionService;
import com.restaurantos.modules.order.event.OrderCompletedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryDeductionServiceImpl implements InventoryDeductionService {

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientService ingredientService;
    // Note: OrderItemRepository will be needed when MODULE 6 (Order) is
    // implemented.
    // For now, this is a structural implementation that will be completed with
    // Order logic.

    @EventListener
    public void handleOrderCompleted(OrderCompletedEvent event) {
        log.info("Received OrderCompletedEvent for order: {}. Triggering inventory deduction.", event.getOrderId());
        deductInventoryForOrder(event.getOrderId());
    }

    @Override
    @Transactional
    public void deductInventoryForOrder(UUID orderId) {
        // TODO: Fetch OrderItems for orderId once MODULE 6 is implemented
        // For each OrderItem:
        // 1. Get MenuItem ID and quantity sold
        // 2. Fetch RecipeIngredients for that MenuItem
        // 3. For each RecipeIngredient:
        // qty_to_deduct = recipe_qty * order_item_qty
        // ingredientService.adjustStock(ingredientId, -qty_to_deduct, AUTO_DEDUCTION,
        // "Order: " + orderId)

        log.warn("Inventory deduction for order {} is partially implemented. Awaiting Order module integration.",
                orderId);
    }
}
