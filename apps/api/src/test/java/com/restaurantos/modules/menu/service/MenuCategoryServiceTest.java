package com.restaurantos.modules.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurantos.modules.menu.dto.request.CategoryRequest;
import com.restaurantos.modules.menu.dto.response.CategoryResponse;
import com.restaurantos.modules.menu.entity.MenuCategory;
import com.restaurantos.modules.menu.repository.MenuCategoryRepository;
import com.restaurantos.modules.menu.service.impl.MenuCategoryServiceImpl;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;

@ExtendWith(MockitoExtension.class)
class MenuCategoryServiceTest {

    @Mock
    private MenuCategoryRepository categoryRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuCategoryServiceImpl categoryService;

    private Restaurant restaurant;
    private UUID restaurantId;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        restaurant = Restaurant.builder().name("Test Rest").build();
        restaurant.setId(restaurantId);
    }

    @Test
    void create_ShouldSaveAndReturnResponse() {
        // Given
        CategoryRequest request = CategoryRequest.builder()
                .name("New Category")
                .displayOrder(1)
                .build();

        MenuCategory savedCategory = MenuCategory.builder()
                .name(request.getName())
                .displayOrder(request.getDisplayOrder())
                .restaurant(restaurant)
                .build();
        savedCategory.setId(UUID.randomUUID());

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(categoryRepository.save(any(MenuCategory.class))).thenReturn(savedCategory);

        // When
        CategoryResponse response = categoryService.create(restaurantId, request);

        // Then
        assertThat(response.getName()).isEqualTo(request.getName());
        verify(categoryRepository).save(any(MenuCategory.class));
    }

    @Test
    void getById_WhenExists_ShouldReturnResponse() {
        // Given
        UUID categoryId = UUID.randomUUID();
        MenuCategory category = MenuCategory.builder()
                .name("Existing")
                .build();
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // When
        CategoryResponse response = categoryService.getById(categoryId);

        // Then
        assertThat(response.getName()).isEqualTo("Existing");
    }
}
