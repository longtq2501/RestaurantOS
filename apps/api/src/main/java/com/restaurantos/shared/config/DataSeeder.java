package com.restaurantos.shared.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.restaurantos.modules.auth.entity.User;
import com.restaurantos.modules.auth.entity.UserRole;
import com.restaurantos.modules.auth.repository.UserRepository;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Seeds initial data for development and testing.
 * Runs on every startup but is idempotent (skips if data already exists).
 */
@Configuration
@Slf4j
@Profile("!test")
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (userRepository.existsByUsername("owner")) {
                log.info("[DataSeeder] Seed data already exists. Skipping.");
                return;
            }

            log.info("[DataSeeder] Seeding initial data...");

            // Create demo restaurant
            Restaurant restaurant = Restaurant.builder()
                    .name("Demo Restaurant")
                    .build();
            restaurant = restaurantRepository.save(restaurant);

            // OWNER
            userRepository.save(User.builder()
                    .username("owner")
                    .email("owner@demo.com")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .fullName("Owner User")
                    .role(UserRole.OWNER)
                    .restaurant(restaurant)
                    .isActive(true)
                    .build());

            // MANAGER
            userRepository.save(User.builder()
                    .username("manager")
                    .email("manager@demo.com")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .fullName("Manager User")
                    .role(UserRole.MANAGER)
                    .restaurant(restaurant)
                    .isActive(true)
                    .build());

            // STAFF
            userRepository.save(User.builder()
                    .username("staff")
                    .email("staff@demo.com")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .fullName("Staff User")
                    .role(UserRole.STAFF)
                    .restaurant(restaurant)
                    .isActive(true)
                    .build());

            // KITCHEN
            userRepository.save(User.builder()
                    .username("kitchen")
                    .email("kitchen@demo.com")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .fullName("Kitchen User")
                    .role(UserRole.KITCHEN)
                    .restaurant(restaurant)
                    .isActive(true)
                    .build());

            log.info("[DataSeeder] Done. Seeded 1 restaurant + 4 users (owner/manager/staff/kitchen).");
        };
    }
}
