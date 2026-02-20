package com.restaurantos.modules.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.restaurantos.modules.restaurant.dto.request.RestaurantUpdateRequest;
import com.restaurantos.modules.restaurant.dto.response.RestaurantResponse;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.entity.SubscriptionPlan;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.modules.restaurant.service.impl.RestaurantServiceImpl;
import com.restaurantos.shared.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    @Test
    void getById_WhenExists_ShouldReturnResponse() {
        // Given
        UUID id = UUID.randomUUID();
        Restaurant restaurant = Restaurant.builder()
                .name("Test Rest")
                .slug("test-rest")
                .plan(SubscriptionPlan.FREE)
                .build();
        restaurant.setId(id); // Simulate DB ID

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

        // When
        RestaurantResponse response = restaurantService.getById(id);

        // Then
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("Test Rest");
        verify(restaurantRepository).findById(id);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        // Given
        UUID id = UUID.randomUUID();
        when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> restaurantService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Restaurant not found");
    }

    @Test
    void getBySlug_WhenExists_ShouldReturnResponse() {
        // Given
        String slug = "test-slug";
        Restaurant restaurant = Restaurant.builder()
                .name("Test Rest")
                .slug(slug)
                .plan(SubscriptionPlan.FREE)
                .build();
        when(restaurantRepository.findBySlug(slug)).thenReturn(Optional.of(restaurant));

        // When
        RestaurantResponse response = restaurantService.getBySlug(slug);

        // Then
        assertThat(response.getSlug()).isEqualTo(slug);
        verify(restaurantRepository).findBySlug(slug);
    }

    @Test
    void update_WhenExists_ShouldUpdateAndReturnResponse() {
        // Given
        UUID id = UUID.randomUUID();
        Restaurant restaurant = Restaurant.builder()
                .name("Old Name")
                .build();
        restaurant.setId(id);

        RestaurantUpdateRequest request = RestaurantUpdateRequest.builder()
                .name("New Name")
                .address("New Address")
                .build();

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        RestaurantResponse response = restaurantService.update(id, request);

        // Then
        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getAddress()).isEqualTo("New Address");
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void updateLogo_WhenExists_ShouldUploadAndSave() {
        // Given
        UUID id = UUID.randomUUID();
        Restaurant restaurant = Restaurant.builder()
                .name("Test Rest")
                .build();
        restaurant.setId(id);

        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        String newLogoUrl = "http://storage/new-logo.png";

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        when(fileStorageService.uploadFile(file, "logos")).thenReturn(newLogoUrl);
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        RestaurantResponse response = restaurantService.updateLogo(id, file);

        // Then
        assertThat(response.getLogoUrl()).isEqualTo(newLogoUrl);
        verify(fileStorageService).uploadFile(file, "logos");
        verify(restaurantRepository).save(restaurant);
    }
}
