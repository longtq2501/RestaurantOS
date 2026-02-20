package com.restaurantos.modules.table.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating or updating a restaurant table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableRequest {

    /** The display number of the table. */
    @NotNull(message = "Table number is required")
    private Integer tableNumber;

    /** Maximum number of people the table can accommodate. */
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    /** Area or zone of the restaurant where the table is located. */
    private String section;
}
