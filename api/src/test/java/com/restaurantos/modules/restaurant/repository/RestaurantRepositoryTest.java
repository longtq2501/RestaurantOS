package com.restaurantos.modules.restaurant.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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

import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.entity.SubscriptionPlan;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = "com.restaurantos")
@EnableJpaRepositories(basePackages = "com.restaurantos")
class RestaurantRepositoryTest {

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
    private RestaurantRepository restaurantRepository;

    @Test
    void findBySlug_WhenExists_ShouldReturnRestaurant() {
        // Given
        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .slug("test-restaurant")
                .plan(SubscriptionPlan.FREE)
                .build();
        entityManager.persist(restaurant);
        entityManager.flush();

        // When
        Optional<Restaurant> found = restaurantRepository.findBySlug("test-restaurant");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Restaurant");
        assertThat(found.get().getSlug()).isEqualTo("test-restaurant");
    }

    @Test
    void findBySlug_WhenNotExists_ShouldReturnEmpty() {
        // When
        Optional<Restaurant> found = restaurantRepository.findBySlug("non-existent-slug");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void existsBySlug_WhenExists_ShouldReturnTrue() {
        // Given
        Restaurant restaurant = Restaurant.builder()
                .name("Another Restaurant")
                .slug("another-restaurant")
                .plan(SubscriptionPlan.STARTER)
                .build();
        entityManager.persist(restaurant);
        entityManager.flush();

        // When
        boolean exists = restaurantRepository.existsBySlug("another-restaurant");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsBySlug_WhenNotExists_ShouldReturnFalse() {
        // When
        boolean exists = restaurantRepository.existsBySlug("non-existent-slug");

        // Then
        assertThat(exists).isFalse();
    }
}
