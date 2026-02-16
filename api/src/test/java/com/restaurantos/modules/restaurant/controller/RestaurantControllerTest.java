package com.restaurantos.modules.restaurant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.restaurantos.modules.restaurant.dto.request.RestaurantUpdateRequest;
import com.restaurantos.modules.restaurant.dto.response.RestaurantResponse;
import com.restaurantos.modules.restaurant.service.RestaurantService;
import com.restaurantos.shared.config.SecurityConfig;

@WebMvcTest(RestaurantController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class RestaurantControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private RestaurantService restaurantService;

        @MockitoBean
        private JwtService jwtService; // Required for SecurityConfig

        @MockitoBean
        private UserDetailsService userDetailsService; // Required for JwtAuthenticationFilter

        @Test
        @WithMockUser(roles = "OWNER")
        void getById_WhenOwner_ShouldReturnRestaurant() throws Exception {
                UUID id = UUID.randomUUID();
                RestaurantResponse response = RestaurantResponse.builder()
                                .id(id)
                                .name("Test Restaurant")
                                .build();

                when(restaurantService.getById(id)).thenReturn(response);

                mockMvc.perform(get("/api/restaurants/{id}", id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.name").value("Test Restaurant"));
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        void getById_WhenCustomer_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(get("/api/restaurants/{id}", UUID.randomUUID()))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "OWNER")
        void update_WhenOwner_ShouldReturnUpdatedRestaurant() throws Exception {
                UUID id = UUID.randomUUID();
                RestaurantUpdateRequest request = RestaurantUpdateRequest.builder()
                                .name("Updated Name")
                                .email("test@example.com")
                                .build();

                RestaurantResponse response = RestaurantResponse.builder()
                                .id(id)
                                .name("Updated Name")
                                .email("test@example.com")
                                .build();

                when(restaurantService.update(eq(id), any(RestaurantUpdateRequest.class))).thenReturn(response);

                mockMvc.perform(put("/api/restaurants/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.name").value("Updated Name"));
        }
}
