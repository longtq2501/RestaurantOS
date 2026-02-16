package com.restaurantos.modules.inventory.entity;

/**
 * Enumeration of possible inventory adjustment types.
 */
public enum AdjustmentType {
    MANUAL, // Manual adjustment by staff
    AUTO_DEDUCTION, // Automated deduction based on sales/recipes
    WASTE, // Stock lost due to expiration or damage
    RESTOCK, // Stock added from suppliers
    CORRECTION // Correcting stock count discrepancies
}
