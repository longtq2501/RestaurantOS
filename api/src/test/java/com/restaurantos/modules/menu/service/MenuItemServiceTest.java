package com.restaurantos.modules.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.restaurantos.modules.menu.dto.request.MenuItemRequest;
import com.restaurantos.modules.menu.dto.response.MenuItemResponse;
import com.restaurantos.modules.menu.entity.MenuCategory;
import com.restaurantos.modules.menu.entity.MenuItem;
import com.restaurantos.modules.menu.repository.MenuCategoryRepository;
import com.restaurantos.modules.menu.repository.MenuItemRepository;
import com.restaurantos.modules.menu.service.impl.MenuItemServiceImpl;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.modules.restaurant.service.FileStorageService;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuCategoryRepository categoryRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    private Restaurant restaurant;
    private MenuCategory category;
    private UUID restaurantId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        restaurant = Restaurant.builder().name("Test Rest").build();
        restaurant.setId(restaurantId);
        category = MenuCategory.builder().name("Test Cat").build();
        category.setId(categoryId);
    }

    @Test
    void create_ShouldSaveAndReturnResponse() {
        // Given
        MenuItemRequest request = MenuItemRequest.builder()
                .name("New Item")
                .price(BigDecimal.valueOf(10.0))
                .categoryId(categoryId)
                .build();

        MenuItem savedItem = MenuItem.builder()
                .name(request.getName())
                .price(request.getPrice())
                .category(category)
                .restaurant(restaurant)
                .build();
        savedItem.setId(UUID.randomUUID());

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(savedItem);

        // When
        MenuItemResponse response = menuItemService.create(restaurantId, request);

        // Then
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getCategoryName()).isEqualTo(category.getName());
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void uploadImage_ShouldUpdateItemWithUrls() {
        // Given
        UUID itemId = UUID.randomUUID();
        MenuItem item = MenuItem.builder()
                .name("Item")
                .restaurant(restaurant)
                .category(category)
                .isActive(true)
                .build();
        item.setId(itemId);

        MultipartFile file = mock(MultipartFile.class);
        String[] urls = { "http://cdn/image.jpg", "http://cdn/thumb.jpg" };

        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(fileStorageService.uploadFileWithThumbnail(eq(file), anyString(), anyInt(), anyInt())).thenReturn(urls);
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        MenuItemResponse response = menuItemService.uploadImage(itemId, file);

        // Then
        assertThat(response.getImageUrl()).isEqualTo(urls[0]);
        assertThat(response.getThumbnailUrl()).isEqualTo(urls[1]);
        verify(fileStorageService).uploadFileWithThumbnail(eq(file), anyString(), eq(300), eq(300));
    }

    @Test
    void delete_ShouldSoftDeleteItem() {
        // Given
        UUID itemId = UUID.randomUUID();
        MenuItem item = MenuItem.builder()
                .category(category)
                .isActive(true)
                .build();
        item.setId(itemId);

        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // When
        menuItemService.delete(itemId);

        // Then
        assertThat(item.getIsActive()).isFalse();
        verify(menuItemRepository).save(item);
    }
}
