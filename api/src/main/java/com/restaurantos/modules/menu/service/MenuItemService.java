package com.restaurantos.modules.menu.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.restaurantos.modules.menu.dto.request.MenuItemRequest;
import com.restaurantos.modules.menu.dto.response.MenuItemResponse;

/**
 * Service interface for menu item operations.
 */
public interface MenuItemService {

    /**
     * Gets all menu items for a restaurant with optional filters.
     *
     * @param restaurantId the restaurant ID
     * @param categoryId   optional category filter
     * @return list of menu items
     */
    List<MenuItemResponse> getAll(UUID restaurantId, UUID categoryId);

    /**
     * Gets a menu item by ID.
     *
     * @param id the item ID
     * @return the item details
     */
    MenuItemResponse getById(UUID id);

    /**
     * Creates a new menu item.
     *
     * @param restaurantId the restaurant ID
     * @param request      the item details
     * @return the created item
     */
    MenuItemResponse create(UUID restaurantId, MenuItemRequest request);

    /**
     * Updates an existing menu item.
     *
     * @param id      the item ID
     * @param request the updated details
     * @return the updated item
     */
    MenuItemResponse update(UUID id, MenuItemRequest request);

    /**
     * Deletes a menu item (soft delete).
     *
     * @param id the item ID
     */
    void delete(UUID id);

    /**
     * Toggles item availability.
     *
     * @param id the item ID
     * @return the updated item
     */
    MenuItemResponse toggleAvailability(UUID id);

    /**
     * Searches for menu items.
     *
     * @param restaurantId the restaurant ID
     * @param query        the search query
     * @return list of matching items
     */
    List<MenuItemResponse> search(UUID restaurantId, String query);

    /**
     * Uploads an image for a menu item and generates a thumbnail.
     *
     * @param id   the item ID
     * @param file the image file
     * @return the updated item
     */
    MenuItemResponse uploadImage(UUID id, MultipartFile file);

    /**
     * Performs bulk updates on menu items.
     *
     * @param ids    list of item IDs
     * @param action the action to perform (e.g., "activate", "deactivate",
     *               "featured", "unfeatured")
     */
    void bulkUpdate(List<UUID> ids, String action);
}
