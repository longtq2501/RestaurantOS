package com.restaurantos.modules.table.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.restaurantos.modules.table.dto.request.TableRequest;
import com.restaurantos.modules.table.dto.response.TableResponse;

/**
 * Service interface for managing restaurant tables.
 */
public interface TableService {

    /**
     * Retrieves all tables for a specific restaurant.
     *
     * @param restaurantId ID of the restaurant.
     * @return List of table responses.
     */
    List<TableResponse> getAllByRestaurant(UUID restaurantId);

    /**
     * Retrieves a single table by its ID.
     *
     * @param id The table ID.
     * @return The table response.
     */
    TableResponse getById(UUID id);

    /**
     * Creates a new table for a restaurant.
     * Includes automatic QR code token generation.
     *
     * @param restaurantId ID of the restaurant to add the table to.
     * @param request      The table details.
     * @return The created table response.
     */
    TableResponse create(UUID restaurantId, TableRequest request);

    /**
     * Updates an existing table's details.
     *
     * @param id      The table ID to update.
     * @param request The new table details.
     * @return The updated table response.
     */
    TableResponse update(UUID id, TableRequest request);

    /**
     * Deletes a table from the restaurant.
     *
     * @param id The table ID to delete.
     */
    void delete(UUID id);

    /**
     * Generates a PDF containing QR codes for all tables in a restaurant.
     *
     * @param restaurantId ID of the restaurant.
     * @return Byte array of the generated PDF.
     */
    byte[] generateQrCodesPdf(UUID restaurantId) throws IOException;
}
