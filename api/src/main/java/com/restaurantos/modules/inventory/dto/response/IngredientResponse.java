package com.restaurantos.modules.inventory.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for ingredient details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponse {
    private UUID id;
    private String name;
    private String unit;
    private BigDecimal currentStock;
    private BigDecimal minStock;
    private BigDecimal costPerUnit;
    private String supplierName;
    private String supplierPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
