import { ApiResponse, Order, OrderItem, OrderItemStatus, OrderStatus } from "@restaurantos/types";
import apiClient from "./client";

export const orderApi = {
  /**
   * Get all orders for a restaurant
   */
  getOrders: async (restaurantId: string, status?: OrderStatus) => {
    const response = await apiClient.get<ApiResponse<Order[]>>(
      `/api/restaurants/${restaurantId}/orders`,
      {
        params: { status },
      }
    );
    return response.data;
  },

  /**
   * Get order by id
   */
  getOrderById: async (id: string) => {
    const response = await apiClient.get<ApiResponse<Order>>(`/api/restaurants/orders/${id}`);
    return response.data;
  },

  /**
   * Update order status
   */
  updateOrderStatus: async (id: string, status: OrderStatus) => {
    const response = await apiClient.put<ApiResponse<Order>>(
      `/api/restaurants/orders/${id}/status`,
      { status }
    );
    return response.data;
  },

  /**
   * Update individual order item status
   */
  updateOrderItemStatus: async (itemId: string, status: OrderItemStatus) => {
    const response = await apiClient.put<ApiResponse<OrderItem>>(
      `/api/restaurants/order-items/${itemId}/status`,
      { status }
    );
    return response.data;
  },

  /**
   * Delete/Cancel order (Owner only)
   */
  deleteOrder: async (id: string) => {
    const response = await apiClient.delete<ApiResponse<void>>(`/api/restaurants/orders/${id}`);
    return response.data;
  },
};
