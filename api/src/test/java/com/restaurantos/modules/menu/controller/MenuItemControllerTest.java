package com.restaurantos.modules.menu.controller;

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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurantos.modules.auth.service.JwtService;
import com.restaurantos.modules.menu.dto.request.MenuItemRequest;
import com.restaurantos.modules.menu.dto.response.MenuItemResponse;
import com.restaurantos.modules.menu.service.MenuItemService;
import com.restaurantos.shared.config.SecurityConfig;

@WebMvcTest(MenuItemController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MenuItemService menuItemService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "OWNER")
    void getAll_ShouldReturnItems() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        when(menuItemService.getAll(eq(restaurantId), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/restaurants/{restaurantId}/items", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void create_ShouldReturnCreated() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        MenuItemRequest request = MenuItemRequest.builder()
                .name("Pho")
                .price(BigDecimal.valueOf(10.0))
                .categoryId(UUID.randomUUID())
                .build();
        MenuItemResponse response = MenuItemResponse.builder().id(UUID.randomUUID()).name("Pho").build();

        when(menuItemService.create(eq(restaurantId), any(MenuItemRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants/{restaurantId}/items", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Pho"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void search_ShouldReturnItems() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        when(menuItemService.search(eq(restaurantId), eq("spicy"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/restaurants/{restaurantId}/items/search", restaurantId)
                .param("q", "spicy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
