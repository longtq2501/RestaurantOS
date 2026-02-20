package com.restaurantos.modules.menu.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.restaurantos.modules.menu.dto.request.CategoryRequest;
import com.restaurantos.modules.menu.dto.response.CategoryResponse;
import com.restaurantos.modules.menu.service.MenuCategoryService;
import com.restaurantos.shared.config.SecurityConfig;

@WebMvcTest(MenuCategoryController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class MenuCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MenuCategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "OWNER")
    void getAll_ShouldReturnCategories() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        when(categoryService.getAllByRestaurant(restaurantId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/restaurants/{restaurantId}/categories", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void create_ShouldReturnCreated() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        CategoryRequest request = CategoryRequest.builder().name("New Cat").build();
        CategoryResponse response = CategoryResponse.builder().id(UUID.randomUUID()).name("New Cat").build();

        when(categoryService.create(eq(restaurantId), any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants/{restaurantId}/categories", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("New Cat"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void create_WhenStaff_ShouldReturnForbidden() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        CategoryRequest request = CategoryRequest.builder().name("New Cat").build();

        mockMvc.perform(post("/api/restaurants/{restaurantId}/categories", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
