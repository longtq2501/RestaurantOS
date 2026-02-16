package com.restaurantos.modules.inventory.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurantos.modules.inventory.entity.InventoryHistory;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, UUID> {

    List<InventoryHistory> findByIngredientIdOrderByCreatedAtDesc(UUID ingredientId);
}
