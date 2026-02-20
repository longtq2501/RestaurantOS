import {
    ApiResponse,
    DashboardSummary,
    RevenueReport,
    TopDish,
} from "@restaurantos/types";
import apiClient from "./client";

export const analyticsApi = {
  getSummary: async (restaurantId: string) => {
    const response = await apiClient.get<ApiResponse<DashboardSummary>>(
      `/api/restaurants/${restaurantId}/dashboard/summary`
    );
    return response.data;
  },

  getRevenueReport: async (
    restaurantId: string,
    startDate: string,
    endDate: string
  ) => {
    const response = await apiClient.get<ApiResponse<RevenueReport>>(
      `/api/restaurants/${restaurantId}/reports/revenue`,
      {
        params: { startDate, endDate },
      }
    );
    return response.data;
  },

  getTopDishes: async (restaurantId: string, period: string = "today") => {
    const response = await apiClient.get<ApiResponse<TopDish[]>>(
      `/api/restaurants/${restaurantId}/reports/top-dishes`,
      {
        params: { period },
      }
    );
    return response.data;
  },

  exportReport: async (restaurantId: string, format: string = "csv") => {
    const response = await apiClient.get(
      `/api/restaurants/${restaurantId}/reports/export`,
      {
        params: { format },
        responseType: "blob",
      }
    );

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", `report-${new Date().toISOString().split('T')[0]}.${format}`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  },
};
