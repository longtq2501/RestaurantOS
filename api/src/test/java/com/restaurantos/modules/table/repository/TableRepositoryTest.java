package com.restaurantos.modules.table.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.modules.table.entity.RestaurantTable;
import com.restaurantos.modules.table.entity.TableStatus;
import com.restaurantos.shared.config.JpaConfig;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class TableRepositoryTest {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .slug("test-restaurant")
                .address("123 Test St")
                .phone("123456789")
                .email("test@restaurant.com")
                .build();
        restaurant = restaurantRepository.save(restaurant);
    }

    @Test
    void should_SaveTable_When_Valid() {
        // Given
        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(101)
                .capacity(4)
                .restaurant(restaurant)
                .qrCodeToken("token123")
                .status(TableStatus.EMPTY)
                .build();

        // When
        RestaurantTable saved = tableRepository.save(table);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTableNumber()).isEqualTo(101);
    }

    @Test
    void should_FindTablesByRestaurant_OrderedByNumber() {
        // Given
        saveTable(103);
        saveTable(101);
        saveTable(102);

        // When
        List<RestaurantTable> tables = tableRepository.findByRestaurantIdOrderByTableNumberAsc(restaurant.getId());

        // Then
        assertThat(tables).hasSize(3);
        assertThat(tables.get(0).getTableNumber()).isEqualTo(101);
        assertThat(tables.get(1).getTableNumber()).isEqualTo(102);
        assertThat(tables.get(2).getTableNumber()).isEqualTo(103);
    }

    @Test
    void should_FindTableByQrCodeToken() {
        // Given
        RestaurantTable table = saveTable(1);
        String token = table.getQrCodeToken();

        // When
        Optional<RestaurantTable> found = tableRepository.findByQrCodeToken(token);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTableNumber()).isEqualTo(1);
    }

    @Test
    void should_ExistByRestaurantAndNumber() {
        // Given
        saveTable(10);

        // When
        boolean exists = tableRepository.existsByRestaurantIdAndTableNumber(restaurant.getId(), 10);
        boolean notExists = tableRepository.existsByRestaurantIdAndTableNumber(restaurant.getId(), 20);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    private RestaurantTable saveTable(Integer number) {
        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(number)
                .capacity(4)
                .restaurant(restaurant)
                .qrCodeToken(UUID.randomUUID().toString())
                .status(TableStatus.EMPTY)
                .build();
        return tableRepository.save(table);
    }
}
