package com.restaurantos.modules.restaurant.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.restaurantos.modules.restaurant.dto.request.RestaurantUpdateRequest;
import com.restaurantos.modules.restaurant.dto.response.RestaurantResponse;
import com.restaurantos.modules.restaurant.service.RestaurantService;
import com.restaurantos.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for managing restaurant profiles.
 */
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * Gets a restaurant's profile by ID.
     * Restricted to OWNER or MANAGER roles.
     *
     * @param id the restaurant identifier
     * @return the restaurant profile
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getById(@PathVariable UUID id) {
        log.info("REST request to get Restaurant: {}", id);
        RestaurantResponse response = restaurantService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Updates a restaurant's profile.
     * Restricted to OWNER role.
     *
     * @param id      the restaurant identifier
     * @param request the update data
     * @return the updated restaurant profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody RestaurantUpdateRequest request) {
        log.info("REST request to update Restaurant: {}", id);
        RestaurantResponse response = restaurantService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated successfully", response));
    }

    /**
     * Uploads a logo for a restaurant.
     * Restricted to OWNER role.
     *
     * @param id   the restaurant identifier
     * @param file the logo file
     * @return the updated restaurant profile with new logo URL
     */
    @PostMapping("/{id}/upload-logo")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> uploadLogo(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        log.info("REST request to upload logo for Restaurant: {}", id);
        RestaurantResponse response = restaurantService.updateLogo(id, file);
        return ResponseEntity.ok(ApiResponse.success("Logo uploaded successfully", response));
    }
}
