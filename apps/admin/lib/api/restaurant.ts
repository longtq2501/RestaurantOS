import {
    ApiResponse,
    Restaurant,
    RestaurantUpdateRequest,
} from "@restaurantos/types";
import apiClient from "./client";

export const restaurantApi = {
  getProfile: async (id: string) => {
    const response = await apiClient.get<ApiResponse<Restaurant>>(
      `/api/restaurants/${id}`
    );
    return response.data;
  },

  updateProfile: async (id: string, data: RestaurantUpdateRequest) => {
    const response = await apiClient.put<ApiResponse<Restaurant>>(
      `/api/restaurants/${id}`,
      data
    );
    return response.data;
  },

  uploadLogo: async (id: string, file: File) => {
    const formData = new FormData();
    formData.append("file", file);

    const response = await apiClient.post<ApiResponse<Restaurant>>(
      `/api/restaurants/${id}/upload-logo`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/multipart-form-data",
        },
      }
    );
    return response.data;
  },
};
