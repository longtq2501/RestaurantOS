export interface Restaurant {
  id: string;
  name: string;
  slug: string;
  address?: string;
  phone?: string;
  email?: string;
  logoUrl?: string;
  themeColor?: string;
  customDomain?: string;
  settings?: string; // JSON string
}

export interface RestaurantUpdateRequest {
  name: string;
  address?: string;
  phone?: string;
  email?: string;
  themeColor?: string;
  customDomain?: string;
  settings?: string;
}
