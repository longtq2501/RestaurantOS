package com.restaurantos.modules.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import com.restaurantos.modules.inventory.dto.request.IngredientRequest;
import com.restaurantos.modules.inventory.dto.request.StockAdjustmentRequest;
import com.restaurantos.modules.inventory.dto.response.IngredientResponse;
import com.restaurantos.modules.inventory.entity.AdjustmentType;
import com.restaurantos.modules.inventory.entity.Ingredient;
import com.restaurantos.modules.inventory.entity.InventoryHistory;
import com.restaurantos.modules.inventory.repository.IngredientRepository;
import com.restaurantos.modules.inventory.repository.InventoryHistoryRepository;
import com.restaurantos.modules.inventory.service.impl.IngredientServiceImpl;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private InventoryHistoryRepository historyRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    private Restaurant restaurant;
    private UUID restaurantId;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        restaurant = Restaurant.builder()
                .name("Test Resto")
                .slug("test-resto")
                .build();
        restaurant.setId(restaurantId);
    }

    @Test
    void create_ShouldReturnSavedIngredient() {
        // Given
        IngredientRequest request = IngredientRequest.builder()
                .name("Tomato")
                .unit("kg")
                .currentStock(BigDecimal.TEN)
                .minStock(BigDecimal.ONE)
                .build();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(invocation -> {
            Ingredient i = invocation.getArgument(0);
            i.setId(UUID.randomUUID());
            return i;
        });

        // When
        IngredientResponse response = ingredientService.create(restaurantId, request);

        // Then
        assertThat(response.getName()).isEqualTo("Tomato");
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    void adjustStock_ShouldUpdateStockAndSaveHistory() {
        // Given
        UUID ingredientId = UUID.randomUUID();
        Ingredient ingredient = Ingredient.builder()
                .name("Tomato")
                .currentStock(BigDecimal.TEN)
                .build();
        ingredient.setId(ingredientId);

        StockAdjustmentRequest request = StockAdjustmentRequest.builder()
                .quantity(BigDecimal.valueOf(5))
                .type(AdjustmentType.RESTOCK)
                .reason("Buying more")
                .build();

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);

        // When
        IngredientResponse response = ingredientService.adjustStock(ingredientId, request);

        // Then
        assertThat(response.getCurrentStock()).isEqualByComparingTo(BigDecimal.valueOf(15));
        verify(historyRepository).save(any(InventoryHistory.class));
        verify(ingredientRepository).save(ingredient);
    }
}
