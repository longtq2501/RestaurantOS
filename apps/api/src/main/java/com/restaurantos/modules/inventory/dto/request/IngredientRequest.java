package com.restaurantos.modules.inventory.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating an ingredient.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientRequest {

    @NotBlank(message = "Ingredient name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Unit is required")
    @Size(max = 20, message = "Unit cannot exceed 20 characters")
    private String unit;

    @NotNull(message = "Current stock is required")
    @DecimalMin(value = "0.0", message = "Current stock cannot be negative")
    private BigDecimal currentStock;

    @NotNull(message = "Minimum stock is required")
    @DecimalMin(value = "0.0", message = "Minimum stock cannot be negative")
    private BigDecimal minStock;

    @DecimalMin(value = "0.0", message = "Cost per unit cannot be negative")
    private BigDecimal costPerUnit;

    @Size(max = 100, message = "Supplier name cannot exceed 100 characters")
    private String supplierName;

    @Size(max = 20, message = "Supplier phone cannot exceed 20 characters")
    private String supplierPhone;
}
