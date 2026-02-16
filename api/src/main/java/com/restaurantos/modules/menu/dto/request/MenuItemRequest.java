package com.restaurantos.modules.menu.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a menu item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequest {

    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name cannot exceed 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price;

    private Integer prepTime;

    @Min(value = 0, message = "Spicy level cannot be negative")
    private Integer spicyLevel;

    private Boolean isVegetarian;

    @Size(max = 500, message = "Allergens cannot exceed 500 characters")
    private String allergens;

    private Boolean isAvailable;

    private Integer displayOrder;

    private Boolean isFeatured;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;
}
