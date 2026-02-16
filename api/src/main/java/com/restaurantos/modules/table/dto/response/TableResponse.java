package com.restaurantos.modules.table.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload for restaurant table details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {

    private UUID id;

    private Integer tableNumber;

    private Integer capacity;

    private String qrCodeToken;

    private String status;

    private String section;

    private UUID currentOrderId;

    private UUID restaurantId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
