package com.restaurantos.modules.restaurant.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.restaurant.entity.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
}
