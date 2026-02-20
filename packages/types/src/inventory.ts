export enum AdjustmentType {
  MANUAL = "MANUAL",
  AUTO_DEDUCTION = "AUTO_DEDUCTION",
  WASTE = "WASTE",
  RESTOCK = "RESTOCK",
  CORRECTION = "CORRECTION",
}

export interface Ingredient {
  id: string;
  name: string;
  unit: string;
  currentStock: number;
  minStock: number;
  costPerUnit: number;
  supplierName?: string;
  supplierPhone?: string;
  createdAt: string;
  updatedAt: string;
}

export interface IngredientRequest {
  name: string;
  unit: string;
  currentStock: number;
  minStock: number;
  costPerUnit?: number;
  supplierName?: string;
  supplierPhone?: string;
}

export interface StockAdjustmentRequest {
  quantity: number;
  type: AdjustmentType;
  reason?: string;
}

export interface RecipeIngredient {
  ingredientId: string;
  ingredientName: string;
  quantity: number;
  unit: string;
}

export interface Recipe {
  menuItemId: string;
  menuItemName: string;
  ingredients: RecipeIngredient[];
}

export interface RecipeRequest {
  ingredients: {
    ingredientId: string;
    quantity: number;
  }[];
}
