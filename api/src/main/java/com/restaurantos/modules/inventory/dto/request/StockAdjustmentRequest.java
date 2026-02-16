package com.restaurantos.modules.inventory.dto.request;

import java.math.BigDecimal;

import com.restaurantos.modules.inventory.entity.AdjustmentType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adjusting ingredient stock.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequest {

    @NotNull(message = "Adjustment quantity is required")
    private BigDecimal quantity;

    @NotNull(message = "Adjustment type is required")
    private AdjustmentType type;

    private String reason;
}
