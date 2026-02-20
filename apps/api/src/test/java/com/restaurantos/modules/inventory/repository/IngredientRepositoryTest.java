package com.restaurantos.modules.inventory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.restaurantos.modules.inventory.entity.Ingredient;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.shared.config.JpaConfig;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
class IngredientRepositoryTest {

        @Autowired
        private TestEntityManager entityManager;

        @Autowired
        private IngredientRepository ingredientRepository;

        private Restaurant restaurant;

        @BeforeEach
        void setUp() {
                restaurant = Restaurant.builder()
                                .name("Test Resto")
                                .slug("test-resto")
                                .build();
                entityManager.persist(restaurant);
        }

        @Test
        void findByRestaurantIdAndCurrentStockLessThanMinStock_ShouldReturnLowStockItems() {
                // Given
                Ingredient i1 = Ingredient.builder()
                                .name("Sugar")
                                .unit("kg")
                                .currentStock(BigDecimal.valueOf(10))
                                .minStock(BigDecimal.valueOf(5))
                                .restaurant(restaurant)
                                .build();

                Ingredient i2 = Ingredient.builder()
                                .name("Salt")
                                .unit("kg")
                                .currentStock(BigDecimal.valueOf(2))
                                .minStock(BigDecimal.valueOf(5))
                                .restaurant(restaurant)
                                .build();

                entityManager.persist(i1);
                entityManager.persist(i2);
                entityManager.flush();

                // When
                List<Ingredient> result = ingredientRepository
                                .findByRestaurantIdAndCurrentStockLessThanMinStock(restaurant.getId());

                // Then
                assertThat(result).hasSize(1);
                assertThat(result.get(0).getName()).isEqualTo("Salt");
        }
}
