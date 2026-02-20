package com.restaurantos.modules.inventory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import com.restaurantos.modules.inventory.dto.request.IngredientRequest;
import com.restaurantos.modules.inventory.dto.request.StockAdjustmentRequest;
import com.restaurantos.modules.inventory.dto.response.IngredientResponse;
import com.restaurantos.modules.inventory.entity.AdjustmentType;
import com.restaurantos.modules.inventory.service.IngredientService;
import com.restaurantos.shared.config.SecurityConfig;

@WebMvcTest(IngredientController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredientService ingredientService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private com.restaurantos.modules.auth.service.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "OWNER")
    void getAll_ShouldReturnList() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        IngredientResponse response = IngredientResponse.builder()
                .name("Tomato")
                .unit("kg")
                .build();

        when(ingredientService.getAllByRestaurant(restaurantId)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/restaurants/{restaurantId}/ingredients", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Tomato"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void create_ShouldReturnCreated() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        IngredientRequest request = IngredientRequest.builder()
                .name("Tomato")
                .unit("kg")
                .currentStock(BigDecimal.TEN)
                .minStock(BigDecimal.ONE)
                .build();

        IngredientResponse response = IngredientResponse.builder()
                .id(UUID.randomUUID())
                .name("Tomato")
                .build();

        when(ingredientService.create(eq(restaurantId), any(IngredientRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants/{restaurantId}/ingredients", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Tomato"));
    }

    @Test
    @WithMockUser(roles = "KITCHEN")
    void adjustStock_ShouldReturnOk() throws Exception {
        UUID ingredientId = UUID.randomUUID();
        StockAdjustmentRequest request = StockAdjustmentRequest.builder()
                .quantity(BigDecimal.valueOf(5))
                .type(AdjustmentType.RESTOCK)
                .build();

        IngredientResponse response = IngredientResponse.builder()
                .id(ingredientId)
                .currentStock(BigDecimal.valueOf(15))
                .build();

        when(ingredientService.adjustStock(eq(ingredientId), any(StockAdjustmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants/{restaurantId}/ingredients/{id}/adjust", UUID.randomUUID(), ingredientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStock").value(15));
    }
}
