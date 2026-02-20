import { ApiResponse } from "./api";

export enum UserRole {
  OWNER = "OWNER",
  MANAGER = "MANAGER",
  STAFF = "STAFF",
  KITCHEN = "KITCHEN",
}

export interface User {
  id: string;
  username: string;
  email: string;
  fullName: string;
  role: UserRole;
  roleDisplayName?: string;
  isActive: boolean;
  avatarUrl?: string;
  restaurantId?: string; // Optional context
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  restaurantName: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export type AuthApiResponse = ApiResponse<AuthResponse>;
