package com.restaurantos.modules.menu.service;

import java.util.List;
import java.util.UUID;

import com.restaurantos.modules.menu.dto.request.CategoryRequest;
import com.restaurantos.modules.menu.dto.response.CategoryResponse;

/**
 * Service interface for menu category operations.
 */
public interface MenuCategoryService {

    /**
     * Gets all categories for a restaurant.
     *
     * @param restaurantId the restaurant ID
     * @return list of categories
     */
    List<CategoryResponse> getAllByRestaurant(UUID restaurantId);

    /**
     * Gets all active categories for a restaurant.
     *
     * @param restaurantId the restaurant ID
     * @return list of active categories
     */
    List<CategoryResponse> getActiveByRestaurant(UUID restaurantId);

    /**
     * Gets a category by ID.
     *
     * @param id the category ID
     * @return the category details
     */
    CategoryResponse getById(UUID id);

    /**
     * Creates a new menu category.
     *
     * @param restaurantId the restaurant ID
     * @param request      the category details
     * @return the created category
     */
    CategoryResponse create(UUID restaurantId, CategoryRequest request);

    /**
     * Updates an existing menu category.
     *
     * @param id      the category ID
     * @param request the updated details
     * @return the updated category
     */
    CategoryResponse update(UUID id, CategoryRequest request);

    /**
     * Deletes a menu category.
     *
     * @param id the category ID
     */
    void delete(UUID id);
}
