import { DashboardSummary } from "@restaurantos/types";
import {
    AlertCircle,
    CreditCard,
    ShoppingBag,
    TrendingDown,
    TrendingUp,
    Users,
} from "lucide-react";

interface AnalyticsStatsProps {
    summary?: DashboardSummary;
    isLoading: boolean;
}

export function AnalyticsStats({ summary, isLoading }: AnalyticsStatsProps) {
    const stats = [
        {
            title: "Doanh thu hôm nay",
            value: summary?.todayRevenue.toLocaleString("vi-VN") + "đ",
            icon: CreditCard,
            description: summary?.revenueGrowthRate
                ? `${summary.revenueGrowthRate > 0 ? "+" : ""}${summary.revenueGrowthRate}% so với hôm qua`
                : "Đang tải...",
            trend: summary?.revenueGrowthRate
                ? summary.revenueGrowthRate > 0
                    ? "up"
                    : "down"
                : "neutral",
        },
        {
            title: "Đơn hàng hôm nay",
            value: summary?.todayOrders || 0,
            icon: ShoppingBag,
            description: "Số lượng đơn hàng đã hoàn thành",
            trend: "neutral",
        },
        {
            title: "Cảnh báo hết kho",
            value: summary?.lowStockAlertsCount || 0,
            icon: AlertCircle,
            description: "Nguyên liệu cần nhập thêm",
            trend: summary?.lowStockAlertsCount ? "down" : "neutral",
        },
        {
            title: "Bàn đang hoạt động",
            value: summary?.activeTablesCount || 0,
            icon: Users,
            description: "Số bàn có khách hiện tại",
            trend: "neutral",
        },
    ];

    return (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            {stats.map((stat, index) => (
                <div
                    key={index}
                    className="rounded-xl border bg-white p-6 shadow-sm transition-all hover:shadow-md"
                >
                    <div className="flex items-center justify-between space-y-0 pb-2">
                        <p className="text-sm font-medium text-slate-500">{stat.title}</p>
                        <div className="rounded-md bg-slate-50 p-2">
                            <stat.icon className="h-4 w-4 text-primary" />
                        </div>
                    </div>
                    <div>
                        <div className="text-2xl font-bold">
                            {isLoading ? (
                                <div className="h-8 w-24 animate-pulse rounded bg-slate-100" />
                            ) : (
                                stat.value
                            )}
                        </div>
                        <p className="text-xs text-slate-500 mt-1 flex items-center gap-1">
                            {stat.trend === "up" && (
                                <TrendingUp className="h-3 w-3 text-green-500" />
                            )}
                            {stat.trend === "down" && (
                                <TrendingDown className="h-3 w-3 text-danger" />
                            )}
                            <span
                                className={
                                    stat.trend === "up"
                                        ? "text-green-500"
                                        : stat.trend === "down"
                                            ? "text-danger"
                                            : ""
                                }
                            >
                                {stat.description}
                            </span>
                        </p>
                    </div>
                </div>
            ))}
        </div>
    );
}
