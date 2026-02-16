package com.restaurantos.modules.restaurant.service;

import java.util.UUID;

import com.restaurantos.modules.restaurant.dto.request.RestaurantUpdateRequest;
import com.restaurantos.modules.restaurant.dto.response.RestaurantResponse;

/**
 * Service interface for restaurant profile management.
 */
public interface RestaurantService {

    /**
     * Retrieves restaurant information by its ID.
     *
     * @param id the unique identifier of the restaurant
     * @return the restaurant response
     */
    RestaurantResponse getById(UUID id);

    /**
     * Retrieves restaurant information by its slug.
     *
     * @param slug the url-friendly name of the restaurant
     * @return the restaurant response
     */
    RestaurantResponse getBySlug(String slug);

    /**
     * Updates an existing restaurant's profile.
     *
     * @param id      the identifier of the restaurant to update
     * @param request the update data
     * @return the updated restaurant response
     */
    RestaurantResponse update(UUID id, RestaurantUpdateRequest request);

    /**
     * Updates the logo of a restaurant.
     *
     * @param id   the identifier of the restaurant
     * @param file the logo file
     * @return the updated restaurant response
     */
    RestaurantResponse updateLogo(UUID id, org.springframework.web.multipart.MultipartFile file);
}
