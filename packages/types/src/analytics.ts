export interface TopDish {
  menuItemId: string;
  menuItemName: string;
  quantity: number;
  revenue: number;
}

export interface DashboardSummary {
  todayRevenue: number;
  todayOrders: number;
  revenueGrowthRate: number;
  topDishes: TopDish[];
  lowStockAlertsCount: number;
  activeTablesCount: number;
}

export interface DailyRevenue {
  date: string;
  revenue: number;
  orders: number;
}

export interface RevenueReport {
  totalRevenue: number;
  totalOrders: number;
  dailyData: DailyRevenue[];
}

export type ReportPeriod = "today" | "yesterday" | "last7days" | "last30days" | "custom";
