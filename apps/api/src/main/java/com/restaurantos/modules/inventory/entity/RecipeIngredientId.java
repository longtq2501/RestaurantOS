package com.restaurantos.modules.inventory.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite key for RecipeIngredient entity.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientId implements Serializable {

    @Column(name = "menu_item_id")
    private UUID menuItemId;

    @Column(name = "ingredient_id")
    private UUID ingredientId;
}
