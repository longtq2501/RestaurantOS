"use client";

import { AnalyticsStats } from "@/components/analytics/AnalyticsStats";
import { RevenueChart } from "@/components/analytics/RevenueChart";
import { TopDishesChart } from "@/components/analytics/TopDishesChart";
import { analyticsApi } from "@/lib/api/analytics";
import { useAuthStore } from "@/store/auth.store";
import { Button } from "@restaurantos/ui";
import { useQuery } from "@tanstack/react-query";
import { Download } from "lucide-react";
import { useState } from "react";

export default function AnalyticsPage() {
    const user = useAuthStore((state) => state.user);
    const [period, setPeriod] = useState<string>("last7days");

    // Date calculation based on period
    const getDates = (p: string) => {
        const end = new Date();
        const start = new Date();

        if (p === "today") {
            start.setHours(0, 0, 0, 0);
        } else if (p === "yesterday") {
            start.setDate(end.getDate() - 1);
            start.setHours(0, 0, 0, 0);
            end.setDate(end.getDate() - 1);
            end.setHours(23, 59, 59, 999);
        } else if (p === "last7days") {
            start.setDate(end.getDate() - 7);
        } else if (p === "last30days") {
            start.setDate(end.getDate() - 30);
        }

        return {
            start: start.toISOString().split("T")[0],
            end: end.toISOString().split("T")[0],
        };
    };

    const dates = getDates(period);

    const { data: summary, isLoading: isSummaryLoading } = useQuery({
        queryKey: ["analytics-summary", user?.restaurantId],
        queryFn: () => (user?.restaurantId ? analyticsApi.getSummary(user.restaurantId) : null),
        enabled: !!user?.restaurantId,
        select: (res: any) => res.data,
    });

    const { data: revenueData, isLoading: isRevenueLoading } = useQuery({
        queryKey: ["analytics-revenue", user?.restaurantId, period],
        queryFn: () =>
            user?.restaurantId
                ? analyticsApi.getRevenueReport(user.restaurantId, dates.start, dates.end)
                : null,
        enabled: !!user?.restaurantId,
        select: (res: any) => res.data,
    });

    const { data: topDishes, isLoading: isTopDishesLoading } = useQuery({
        queryKey: ["analytics-top-dishes", user?.restaurantId, period],
        queryFn: () =>
            user?.restaurantId ? analyticsApi.getTopDishes(user.restaurantId, period) : null,
        enabled: !!user?.restaurantId,
        select: (res: any) => res.data,
    });

    const handleExport = () => {
        if (user?.restaurantId) {
            analyticsApi.exportReport(user.restaurantId);
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Báo cáo & Phân tích</h1>
                    <p className="text-slate-500">
                        Tổng quan hiệu quả kinh doanh của nhà hàng
                    </p>
                </div>
                <div className="flex items-center gap-2">
                    <Button variant="outline" onClick={handleExport}>
                        <Download className="mr-2 h-4 w-4" />
                        Xuất báo cáo
                    </Button>
                </div>
            </div>

            <div className="flex items-center gap-2 rounded-lg bg-white p-1 border shadow-sm w-fit">
                {[
                    { label: "Hôm nay", value: "today" },
                    { label: "Hôm qua", value: "yesterday" },
                    { label: "7 ngày qua", value: "last7days" },
                    { label: "30 ngày qua", value: "last30days" },
                ].map((item) => (
                    <button
                        key={item.value}
                        className={`px-4 py-1.5 text-sm font-medium rounded-md transition-all ${period === item.value
                                ? "bg-slate-900 text-white shadow-sm"
                                : "text-slate-500 hover:text-slate-900 hover:bg-slate-50"
                            }`}
                        onClick={() => setPeriod(item.value)}
                    >
                        {item.label}
                    </button>
                ))}
            </div>

            <AnalyticsStats summary={summary} isLoading={isSummaryLoading} />

            <div className="grid gap-6 lg:grid-cols-3">
                <div className="lg:col-span-2">
                    <RevenueChart
                        data={revenueData?.dailyData || []}
                        isLoading={isRevenueLoading}
                    />
                </div>
                <div>
                    <TopDishesChart
                        data={topDishes || []}
                        isLoading={isTopDishesLoading}
                    />
                </div>
            </div>
        </div>
    );
}
