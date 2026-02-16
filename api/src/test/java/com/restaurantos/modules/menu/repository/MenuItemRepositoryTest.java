package com.restaurantos.modules.menu.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import com.restaurantos.modules.menu.entity.MenuCategory;
import com.restaurantos.modules.menu.entity.MenuItem;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.entity.SubscriptionPlan;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = "com.restaurantos")
@EnableJpaRepositories(basePackages = "com.restaurantos")
class MenuItemRepositoryTest {

    @TestConfiguration
    @EnableJpaAuditing
    static class TestAuditConfig {
        @Bean
        public AuditorAware<String> auditorProvider() {
            return () -> Optional.of("test-user");
        }
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MenuItemRepository menuItemRepository;

    private Restaurant restaurant;
    private MenuCategory category;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .slug("test-restaurant")
                .plan(SubscriptionPlan.STARTER)
                .build();
        entityManager.persist(restaurant);

        category = MenuCategory.builder()
                .name("Main Course")
                .restaurant(restaurant)
                .build();
        entityManager.persist(category);
        entityManager.flush();
    }

    @Test
    void search_ShouldReturnMatchingItems() {
        // Given
        MenuItem item1 = MenuItem.builder()
                .name("Spicy Pho")
                .description("Beef noodle soup")
                .price(BigDecimal.valueOf(15.99))
                .restaurant(restaurant)
                .category(category)
                .build();
        MenuItem item2 = MenuItem.builder()
                .name("Pad Thai")
                .description("Stir-fried spicy noodles")
                .price(BigDecimal.valueOf(14.50))
                .restaurant(restaurant)
                .category(category)
                .build();
        MenuItem item3 = MenuItem.builder()
                .name("Burger")
                .description("Classic beef burger")
                .price(BigDecimal.valueOf(12.00))
                .restaurant(restaurant)
                .category(category)
                .build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.flush();

        // When
        List<MenuItem> result = menuItemRepository.search(restaurant.getId(), "spicy");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(MenuItem::getName).containsExactlyInAnyOrder("Spicy Pho", "Pad Thai");
    }

    @Test
    void findByCategoryId_ShouldReturnItemsInCategory() {
        // Given
        MenuItem item1 = MenuItem.builder()
                .name("Item 1")
                .price(BigDecimal.TEN)
                .restaurant(restaurant)
                .category(category)
                .build();
        entityManager.persist(item1);
        entityManager.flush();

        // When
        List<MenuItem> found = menuItemRepository.findByCategoryId(category.getId());

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Item 1");
    }
}
