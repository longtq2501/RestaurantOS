package com.restaurantos.modules.inventory.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for recipe details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponse {
    private UUID menuItemId;
    private String menuItemName;
    private List<RecipeIngredientResponse> ingredients;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeIngredientResponse {
        private UUID ingredientId;
        private String ingredientName;
        private BigDecimal quantity;
        private String unit;
    }
}
