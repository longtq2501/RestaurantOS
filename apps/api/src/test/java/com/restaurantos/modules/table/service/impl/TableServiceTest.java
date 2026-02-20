package com.restaurantos.modules.table.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.modules.table.dto.request.TableRequest;
import com.restaurantos.modules.table.dto.response.TableResponse;
import com.restaurantos.modules.table.entity.RestaurantTable;
import com.restaurantos.modules.table.entity.TableStatus;
import com.restaurantos.modules.table.repository.TableRepository;
import com.restaurantos.modules.table.service.PDFService;
import com.restaurantos.modules.table.service.QRCodeService;
import com.restaurantos.shared.exception.AlreadyExistsException;
import com.restaurantos.shared.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @Mock
    private TableRepository tableRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private QRCodeService qrCodeService;

    @Mock
    private PDFService pdfService;

    @InjectMocks
    private TableServiceImpl tableService;

    private Restaurant restaurant;
    private UUID restaurantId;
    private RestaurantTable table;
    private UUID tableId;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .build();
        restaurant.setId(restaurantId);

        tableId = UUID.randomUUID();
        table = RestaurantTable.builder()
                .tableNumber(1)
                .capacity(4)
                .restaurant(restaurant)
                .status(TableStatus.EMPTY)
                .build();
        table.setId(tableId);
    }

    @Test
    void should_CreateTable_When_Valid() {
        // Given
        TableRequest request = TableRequest.builder()
                .tableNumber(1)
                .capacity(4)
                .build();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(tableRepository.existsByRestaurantIdAndTableNumber(restaurantId, 1)).thenReturn(false);
        when(tableRepository.save(any(RestaurantTable.class))).thenReturn(table);

        // When
        TableResponse response = tableService.create(restaurantId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTableNumber()).isEqualTo(1);
        verify(tableRepository).save(any(RestaurantTable.class));
    }

    @Test
    void should_ThrowException_When_TableNumberExists() {
        // Given
        TableRequest request = TableRequest.builder()
                .tableNumber(1)
                .capacity(4)
                .build();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(tableRepository.existsByRestaurantIdAndTableNumber(restaurantId, 1)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> tableService.create(restaurantId, request))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void should_FindTableById() {
        // Given
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

        // When
        TableResponse response = tableService.getById(tableId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(tableId);
    }

    @Test
    void should_ThrowException_When_TableNotFound() {
        // Given
        when(tableRepository.findById(tableId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tableService.getById(tableId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_DeleteTable() {
        // Given
        when(tableRepository.existsById(tableId)).thenReturn(true);

        // When
        tableService.delete(tableId);

        // Then
        verify(tableRepository).deleteById(tableId);
    }
}
