package com.restaurantos.modules.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurantos.modules.auth.service.JwtService;
import com.restaurantos.modules.order.dto.request.CreateOrderRequest;
import com.restaurantos.modules.order.dto.request.UpdateOrderStatusRequest;
import com.restaurantos.modules.order.dto.response.OrderResponse;
import com.restaurantos.modules.order.entity.OrderStatus;
import com.restaurantos.modules.order.entity.PaymentMethod;
import com.restaurantos.modules.order.service.OrderItemService;
import com.restaurantos.modules.order.service.OrderService;
import com.restaurantos.shared.config.SecurityConfig;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderItemService orderItemService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private com.restaurantos.modules.auth.service.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "STAFF")
    void getAll_ShouldReturnList() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        OrderResponse response = OrderResponse.builder()
                .orderNumber("2602160001")
                .status(OrderStatus.PENDING)
                .build();

        when(orderService.getAll(eq(restaurantId), any())).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/restaurants/{restaurantId}/orders", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].orderNumber").value("2602160001"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void create_ShouldReturnCreated() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        CreateOrderRequest request = CreateOrderRequest.builder()
                .paymentMethod(PaymentMethod.CASH)
                .items(Collections.singletonList(
                        CreateOrderRequest.OrderItemRequest.builder()
                                .menuItemId(UUID.randomUUID())
                                .quantity(1)
                                .build()))
                .build();

        OrderResponse response = OrderResponse.builder()
                .id(UUID.randomUUID())
                .orderNumber("2602160001")
                .build();

        when(orderService.create(eq(restaurantId), any(CreateOrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants/{restaurantId}/orders", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.orderNumber").value("2602160001"));
    }

    @Test
    @WithMockUser(roles = "KITCHEN")
    void updateStatus_ShouldReturnOk() throws Exception {
        UUID orderId = UUID.randomUUID();
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .status(OrderStatus.PREPARING)
                .build();

        OrderResponse response = OrderResponse.builder()
                .id(orderId)
                .status(OrderStatus.PREPARING)
                .build();

        when(orderService.updateStatus(eq(orderId), any(UpdateOrderStatusRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/restaurants/{restaurantId}/orders/{id}/status", UUID.randomUUID(), orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PREPARING"));
    }
}
