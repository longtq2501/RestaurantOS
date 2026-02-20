package com.restaurantos.modules.analytics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.restaurantos.modules.analytics.dto.response.DashboardSummaryResponse;
import com.restaurantos.modules.analytics.dto.response.TopDishResponse;
import com.restaurantos.modules.analytics.service.impl.DashboardServiceImpl;
import com.restaurantos.modules.inventory.repository.IngredientRepository;
import com.restaurantos.modules.order.entity.Order;
import com.restaurantos.modules.order.entity.OrderStatus;
import com.restaurantos.modules.order.repository.OrderItemRepository;
import com.restaurantos.modules.order.repository.OrderRepository;
import com.restaurantos.modules.table.entity.RestaurantTable;
import com.restaurantos.modules.table.entity.TableStatus;
import com.restaurantos.modules.table.repository.TableRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private TableRepository tableRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private UUID restaurantId;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
    }

    @Test
    void getSummary_ShouldReturnCorrectMetrics() {
        // Given
        Order order = Order.builder()
                .totalAmount(BigDecimal.valueOf(500000))
                .status(OrderStatus.COMPLETED)
                .build();

        when(orderRepository.findByRestaurantIdAndCreatedAtBetween(eq(restaurantId), any(), any()))
                .thenReturn(Collections.singletonList(order)) // Today
                .thenReturn(Collections.emptyList()); // Yesterday

        TopDishResponse topDish = TopDishResponse.builder()
                .menuItemName("Pizza")
                .quantity(5L)
                .revenue(BigDecimal.valueOf(1000000))
                .build();

        when(orderItemRepository.findTopSellingItems(eq(restaurantId), any(), any(), any(Pageable.class)))
                .thenReturn(Collections.singletonList(topDish));

        when(ingredientRepository.findByRestaurantIdAndCurrentStockLessThanMinStock(restaurantId))
                .thenReturn(Collections.emptyList());

        RestaurantTable table = RestaurantTable.builder()
                .status(TableStatus.OCCUPIED)
                .build();

        when(tableRepository.findByRestaurantIdOrderByTableNumberAsc(restaurantId))
                .thenReturn(Collections.singletonList(table));

        // When
        DashboardSummaryResponse response = dashboardService.getSummary(restaurantId);

        // Then
        assertThat(response.getTodayRevenue()).isEqualByComparingTo(BigDecimal.valueOf(500000));
        assertThat(response.getTodayOrders()).isEqualTo(1);
        assertThat(response.getTopDishes()).hasSize(1);
        assertThat(response.getTopDishes().get(0).getMenuItemName()).isEqualTo("Pizza");
        assertThat(response.getActiveTablesCount()).isEqualTo(1);
        assertThat(response.getLowStockAlertsCount()).isEqualTo(0);
    }
}
