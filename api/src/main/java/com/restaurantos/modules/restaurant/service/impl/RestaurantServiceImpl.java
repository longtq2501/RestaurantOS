package com.restaurantos.modules.restaurant.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.restaurant.dto.request.RestaurantUpdateRequest;
import com.restaurantos.modules.restaurant.dto.response.RestaurantResponse;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.modules.restaurant.service.FileStorageService;
import com.restaurantos.modules.restaurant.service.RestaurantService;
import com.restaurantos.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link RestaurantService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getById(UUID id) {
        log.info("Fetching restaurant by id: {}", id);
        return restaurantRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getBySlug(String slug) {
        log.info("Fetching restaurant by slug: {}", slug);
        return restaurantRepository.findBySlug(slug)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with slug: " + slug));
    }

    @Override
    @Transactional
    public RestaurantResponse update(UUID id, RestaurantUpdateRequest request) {
        log.info("Updating restaurant profile: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        restaurant.setEmail(request.getEmail());
        restaurant.setThemeColor(request.getThemeColor());
        restaurant.setCustomDomain(request.getCustomDomain());
        restaurant.setSettings(request.getSettings());

        return mapToResponse(restaurantRepository.save(restaurant));
    }

    @Override
    @Transactional
    public RestaurantResponse updateLogo(UUID id, org.springframework.web.multipart.MultipartFile file) {
        log.info("Updating restaurant logo: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

        // Delete old logo if exists
        if (restaurant.getLogoUrl() != null) {
            fileStorageService.deleteFile(restaurant.getLogoUrl());
        }

        String logoUrl = fileStorageService.uploadFile(file, "logos");
        restaurant.setLogoUrl(logoUrl);

        return mapToResponse(restaurantRepository.save(restaurant));
    }

    /**
     * Maps {@link Restaurant} entity to {@link RestaurantResponse} DTO.
     */
    private RestaurantResponse mapToResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .slug(restaurant.getSlug())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .email(restaurant.getEmail())
                .logoUrl(restaurant.getLogoUrl())
                .themeColor(restaurant.getThemeColor())
                .customDomain(restaurant.getCustomDomain())
                .plan(restaurant.getPlan())
                .planDisplayName(restaurant.getPlan().getDisplayName())
                .subscriptionExpiresAt(restaurant.getSubscriptionExpiresAt())
                .settings(restaurant.getSettings())
                .build();
    }
}
