package com.restaurantos.modules.inventory.service;

import java.util.UUID;

/**
 * Service interface for automated inventory deduction.
 */
public interface InventoryDeductionService {
    void deductInventoryForOrder(UUID orderId);
}
