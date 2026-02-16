package com.restaurantos.modules.menu.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.restaurantos.modules.menu.dto.request.MenuItemRequest;
import com.restaurantos.modules.menu.dto.response.MenuItemResponse;
import com.restaurantos.modules.menu.entity.MenuCategory;
import com.restaurantos.modules.menu.entity.MenuItem;
import com.restaurantos.modules.menu.repository.MenuCategoryRepository;
import com.restaurantos.modules.menu.repository.MenuItemRepository;
import com.restaurantos.modules.menu.service.MenuItemService;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.modules.restaurant.service.FileStorageService;
import com.restaurantos.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link MenuItemService}.
 */
@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAll(UUID restaurantId, UUID categoryId) {
        List<MenuItem> items;
        if (categoryId != null) {
            items = menuItemRepository.findByCategoryId(categoryId);
        } else {
            items = menuItemRepository.findByRestaurantId(restaurantId);
        }
        return items.stream()
                .filter(MenuItem::getIsActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse getById(UUID id) {
        MenuItem item = menuItemRepository.findById(id)
                .filter(MenuItem::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found with id: " + id));
        return mapToResponse(item);
    }

    @Override
    @Transactional
    public MenuItemResponse create(UUID restaurantId, MenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        MenuCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu Category not found with id: " + request.getCategoryId()));

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .prepTime(request.getPrepTime())
                .spicyLevel(request.getSpicyLevel() != null ? request.getSpicyLevel() : 0)
                .isVegetarian(request.getIsVegetarian() != null ? request.getIsVegetarian() : false)
                .allergens(request.getAllergens())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .restaurant(restaurant)
                .category(category)
                .build();

        return mapToResponse(menuItemRepository.save(item));
    }

    @Override
    @Transactional
    public MenuItemResponse update(UUID id, MenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .filter(MenuItem::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found with id: " + id));

        MenuCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu Category not found with id: " + request.getCategoryId()));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setPrepTime(request.getPrepTime());
        item.setSpicyLevel(request.getSpicyLevel());
        item.setIsVegetarian(request.getIsVegetarian());
        item.setAllergens(request.getAllergens());
        item.setIsAvailable(request.getIsAvailable());
        item.setDisplayOrder(request.getDisplayOrder());
        item.setIsFeatured(request.getIsFeatured());
        item.setCategory(category);

        return mapToResponse(menuItemRepository.save(item));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found with id: " + id));
        item.setIsActive(false);
        menuItemRepository.save(item);
    }

    @Override
    @Transactional
    public MenuItemResponse toggleAvailability(UUID id) {
        MenuItem item = menuItemRepository.findById(id)
                .filter(MenuItem::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found with id: " + id));
        item.setIsAvailable(!item.getIsAvailable());
        return mapToResponse(menuItemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> search(UUID restaurantId, String query) {
        return menuItemRepository.search(restaurantId, query).stream()
                .filter(MenuItem::getIsActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MenuItemResponse uploadImage(UUID id, MultipartFile file) {
        MenuItem item = menuItemRepository.findById(id)
                .filter(MenuItem::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found with id: " + id));

        // Generate folder name: menu/{restaurant_id}
        String folder = "menu/" + item.getRestaurant().getId().toString();

        // Upload and get URLs
        String[] urls = fileStorageService.uploadFileWithThumbnail(file, folder, 300, 300);

        // Old files should be deleted? In a real system, yes. For now, keep it simple.
        if (item.getImageUrl() != null) {
            fileStorageService.deleteFile(item.getImageUrl());
        }
        if (item.getThumbnailUrl() != null) {
            fileStorageService.deleteFile(item.getThumbnailUrl());
        }

        item.setImageUrl(urls[0]);
        item.setThumbnailUrl(urls[1]);

        return mapToResponse(menuItemRepository.save(item));
    }

    @Override
    @Transactional
    public void bulkUpdate(List<UUID> ids, String action) {
        List<MenuItem> items = menuItemRepository.findAllById(ids);
        for (MenuItem item : items) {
            switch (action.toLowerCase()) {
                case "activate":
                    item.setIsAvailable(true);
                    break;
                case "deactivate":
                    item.setIsAvailable(false);
                    break;
                case "featured":
                    item.setIsFeatured(true);
                    break;
                case "unfeatured":
                    item.setIsFeatured(false);
                    break;
                case "delete":
                    item.setIsActive(false);
                    break;
            }
        }
        menuItemRepository.saveAll(items);
    }

    private MenuItemResponse mapToResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .thumbnailUrl(item.getThumbnailUrl())
                .prepTime(item.getPrepTime())
                .spicyLevel(item.getSpicyLevel())
                .isVegetarian(item.getIsVegetarian())
                .allergens(item.getAllergens())
                .isAvailable(item.getIsAvailable())
                .displayOrder(item.getDisplayOrder())
                .isFeatured(item.getIsFeatured())
                .orderCount(item.getOrderCount())
                .ratingAvg(item.getRatingAvg())
                .ratingCount(item.getRatingCount())
                .categoryId(item.getCategory().getId())
                .categoryName(item.getCategory().getName())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
