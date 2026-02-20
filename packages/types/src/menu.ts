export interface Category {
  id: string;
  name: string;
  description?: string;
  displayOrder: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface MenuItem {
  id: string;
  name: string;
  description?: string;
  price: number;
  imageUrl?: string;
  thumbnailUrl?: string;
  prepTime?: number; // minutes
  spicyLevel?: number;
  isVegetarian?: boolean;
  allergens?: string;
  isAvailable: boolean;
  displayOrder: number;
  isFeatured: boolean;
  orderCount: number;
  ratingAvg: number;
  ratingCount: number;
  categoryId: string;
  categoryName?: string;
  createdAt: string;
  updatedAt: string;
}

// Request Types
export interface CreateCategoryRequest {
  name: string;
  description?: string;
  displayOrder?: number;
}

export interface CreateMenuItemRequest {
  name: string;
  description?: string;
  price: number;
  categoryId: string;
  imageUrl?: string;
  prepTime?: number;
  spicyLevel?: number;
  isVegetarian?: boolean;
  allergens?: string;
  isAvailable?: boolean;
  displayOrder?: number;
  isFeatured?: boolean;
}
