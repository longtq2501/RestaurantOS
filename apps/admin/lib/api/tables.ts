import { ApiResponse, CreateTableRequest, Table, UpdateTableRequest } from "@restaurantos/types";
import apiClient from "./client";

export const tableApi = {
  getTables: async (restaurantId: string) => {
    const response = await apiClient.get<ApiResponse<Table[]>>(
      `/api/restaurants/${restaurantId}/tables`
    );
    return response.data;
  },

  getTableById: async (restaurantId: string, id: string) => {
    const response = await apiClient.get<ApiResponse<Table>>(
      `/api/restaurants/${restaurantId}/tables/${id}`
    );
    return response.data;
  },

  createTable: async (restaurantId: string, data: CreateTableRequest) => {
    const response = await apiClient.post<ApiResponse<Table>>(
      `/api/restaurants/${restaurantId}/tables`,
      data
    );
    return response.data;
  },

  updateTable: async (restaurantId: string, id: string, data: UpdateTableRequest) => {
    const response = await apiClient.put<ApiResponse<Table>>(
      `/api/restaurants/${restaurantId}/tables/${id}`,
      data
    );
    return response.data;
  },

  deleteTable: async (restaurantId: string, id: string) => {
    const response = await apiClient.delete<ApiResponse<void>>(
      `/api/restaurants/${restaurantId}/tables/${id}`
    );
    return response.data;
  },

  downloadQrCodes: async (restaurantId: string) => {
    const response = await apiClient.get(
      `/api/restaurants/${restaurantId}/tables/qr-codes`,
      { responseType: "blob" }
    );
    
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", `table-qr-codes-${restaurantId}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  },
};
