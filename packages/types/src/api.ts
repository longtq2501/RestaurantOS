export interface ApiResponse<T = any> {
  timestamp: string;
  success: boolean;
  message: string;
  data: T;
}
