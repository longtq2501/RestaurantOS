package com.restaurantos.modules.analytics.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for top-selling dishes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopDishResponse {
    private UUID menuItemId;
    private String menuItemName;
    private Long quantity;
    private BigDecimal revenue;
}
