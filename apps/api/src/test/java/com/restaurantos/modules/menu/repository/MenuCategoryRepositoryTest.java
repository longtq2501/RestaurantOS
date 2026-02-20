package com.restaurantos.modules.menu.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.entity.SubscriptionPlan;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = "com.restaurantos")
@EnableJpaRepositories(basePackages = "com.restaurantos")
class MenuCategoryRepositoryTest {

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
    private MenuCategoryRepository categoryRepository;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .slug("test-restaurant")
                .plan(SubscriptionPlan.FREE)
                .build();
        entityManager.persist(restaurant);
        entityManager.flush();
    }

    @Test
    void findByRestaurantIdAndIsActiveTrueOrderByDisplayOrderAsc_ShouldReturnActiveCategories() {
        // Given
        MenuCategory cat1 = MenuCategory.builder()
                .name("Starters")
                .displayOrder(2)
                .isActive(true)
                .restaurant(restaurant)
                .build();
        MenuCategory cat2 = MenuCategory.builder()
                .name("Mains")
                .displayOrder(1)
                .isActive(true)
                .restaurant(restaurant)
                .build();
        MenuCategory cat3 = MenuCategory.builder()
                .name("Hidden")
                .displayOrder(0)
                .isActive(false)
                .restaurant(restaurant)
                .build();

        entityManager.persist(cat1);
        entityManager.persist(cat2);
        entityManager.persist(cat3);
        entityManager.flush();

        // When
        List<MenuCategory> found = categoryRepository
                .findByRestaurantIdAndIsActiveTrueOrderByDisplayOrderAsc(restaurant.getId());

        // Then
        assertThat(found).hasSize(2);
        assertThat(found.get(0).getName()).isEqualTo("Mains");
        assertThat(found.get(1).getName()).isEqualTo("Starters");
    }

    @Test
    void findByRestaurantIdOrderByDisplayOrderAsc_ShouldReturnAllCategories() {
        // Given
        MenuCategory cat1 = MenuCategory.builder()
                .name("Dessert")
                .displayOrder(1)
                .isActive(false)
                .restaurant(restaurant)
                .build();
        entityManager.persist(cat1);
        entityManager.flush();

        // When
        List<MenuCategory> found = categoryRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurant.getId());

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(c -> c.getName().equals("Dessert"));
    }
}
