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
import com.restaurantos.modules.inventory.dto.request.RecipeRequest;
import com.restaurantos.modules.inventory.dto.response.RecipeResponse;
import com.restaurantos.modules.inventory.service.RecipeService;
import com.restaurantos.shared.config.SecurityConfig;

@WebMvcTest(RecipeController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private com.restaurantos.modules.auth.service.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "OWNER")
    void getRecipe_ShouldReturnRecipe() throws Exception {
        UUID menuItemId = UUID.randomUUID();
        RecipeResponse response = RecipeResponse.builder()
                .menuItemId(menuItemId)
                .menuItemName("Pho")
                .ingredients(Collections.emptyList())
                .build();

        when(recipeService.getRecipeByMenuItem(menuItemId)).thenReturn(response);

        mockMvc.perform(get("/api/menu-items/{menuItemId}/recipe", menuItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.menuItemName").value("Pho"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void saveRecipe_ShouldReturnSavedRecipe() throws Exception {
        UUID menuItemId = UUID.randomUUID();
        RecipeRequest request = RecipeRequest.builder()
                .ingredients(Collections.singletonList(
                        RecipeRequest.RecipeIngredientRequest.builder()
                                .ingredientId(UUID.randomUUID())
                                .quantity(BigDecimal.valueOf(200))
                                .build()))
                .build();

        RecipeResponse response = RecipeResponse.builder()
                .menuItemId(menuItemId)
                .ingredients(Collections.emptyList())
                .build();

        when(recipeService.saveRecipe(eq(menuItemId), any(RecipeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/menu-items/{menuItemId}/recipe", menuItemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.menuItemId").exists());
    }
}
