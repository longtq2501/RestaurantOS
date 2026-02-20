import {
    ApiResponse,
    Ingredient,
    IngredientRequest,
    Recipe,
    RecipeRequest,
    StockAdjustmentRequest,
} from "@restaurantos/types";
import apiClient from "./client";

export const inventoryApi = {
  // Ingredients
  getAll: async (restaurantId: string) => {
    const response = await apiClient.get<ApiResponse<Ingredient[]>>(
      `/api/restaurants/${restaurantId}/ingredients`
    );
    return response.data;
  },

  getLowStock: async (restaurantId: string) => {
    const response = await apiClient.get<ApiResponse<Ingredient[]>>(
      `/api/restaurants/${restaurantId}/ingredients/low-stock`
    );
    return response.data;
  },

  getIngredientById: async (restaurantId: string, id: string) => {
    const response = await apiClient.get<ApiResponse<Ingredient>>(
      `/api/restaurants/${restaurantId}/ingredients/${id}`
    );
    return response.data;
  },

  createIngredient: async (restaurantId: string, data: IngredientRequest) => {
    const response = await apiClient.post<ApiResponse<Ingredient>>(
      `/api/restaurants/${restaurantId}/ingredients`,
      data
    );
    return response.data;
  },

  updateIngredient: async (restaurantId: string, id: string, data: IngredientRequest) => {
    const response = await apiClient.put<ApiResponse<Ingredient>>(
      `/api/restaurants/${restaurantId}/ingredients/${id}`,
      data
    );
    return response.data;
  },

  adjustStock: async (restaurantId: string, id: string, data: StockAdjustmentRequest) => {
    const response = await apiClient.post<ApiResponse<Ingredient>>(
      `/api/restaurants/${restaurantId}/ingredients/${id}/adjust`,
      data
    );
    return response.data;
  },

  deleteIngredient: async (restaurantId: string, id: string) => {
    const response = await apiClient.delete<ApiResponse<void>>(
      `/api/restaurants/${restaurantId}/ingredients/${id}`
    );
    return response.data;
  },

  // Recipes
  getRecipe: async (menuItemId: string) => {
    const response = await apiClient.get<ApiResponse<Recipe>>(
      `/api/menu-items/${menuItemId}/recipe`
    );
    return response.data;
  },

  saveRecipe: async (menuItemId: string, data: RecipeRequest) => {
    const response = await apiClient.post<ApiResponse<Recipe>>(
      `/api/menu-items/${menuItemId}/recipe`,
      data
    );
    return response.data;
  },

  deleteRecipe: async (menuItemId: string) => {
    const response = await apiClient.delete<ApiResponse<void>>(
      `/api/menu-items/${menuItemId}/recipe`
    );
    return response.data;
  },
};
