"use client";

import { DailyRevenue } from "@restaurantos/types";
import {
    CartesianGrid,
    Legend,
    Line,
    LineChart,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from "recharts";

interface RevenueChartProps {
    data: DailyRevenue[];
    isLoading: boolean;
}

export function RevenueChart({ data, isLoading }: RevenueChartProps) {
    if (isLoading) {
        return (
            <div className="flex h-[400px] w-full items-center justify-center rounded-xl border bg-white border-slate-100">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        );
    }

    const formattedData = data.map((item) => ({
        ...item,
        formattedDate: new Date(item.date).toLocaleDateString("vi-VN", {
            day: "2-digit",
            month: "2-digit",
        }),
    }));

    return (
        <div className="rounded-xl border bg-white p-6 shadow-sm">
            <div className="mb-6">
                <h3 className="text-lg font-semibold">Biểu đồ doanh thu & Đơn hàng</h3>
                <p className="text-sm text-slate-500">
                    Theo dõi diễn biến doanh thu và lượng đơn hàng theo thời gian
                </p>
            </div>
            <div className="h-[350px] w-full">
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={formattedData}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                        <XAxis
                            dataKey="formattedDate"
                            axisLine={false}
                            tickLine={false}
                            tick={{ fill: "#64748b", fontSize: 12 }}
                            dy={10}
                        />
                        <YAxis
                            yAxisId="left"
                            axisLine={false}
                            tickLine={false}
                            tick={{ fill: "#64748b", fontSize: 12 }}
                            tickFormatter={(value) => `${(value / 1000000).toFixed(1)}M`}
                        />
                        <YAxis
                            yAxisId="right"
                            orientation="right"
                            axisLine={false}
                            tickLine={false}
                            tick={{ fill: "#64748b", fontSize: 12 }}
                        />
                        <Tooltip
                            contentStyle={{
                                backgroundColor: "#fff",
                                border: "1px solid #e2e8f0",
                                borderRadius: "8px",
                                boxShadow: "0 4px 6px -1px rgb(0 0 0 / 0.1)",
                            }}
                            formatter={(value: any, name: string) => {
                                if (name === "revenue")
                                    return [value.toLocaleString("vi-VN") + "đ", "Doanh thu"];
                                if (name === "orders") return [value, "Đơn hàng"];
                                return [value, name];
                            }}
                        />
                        <Legend verticalAlign="top" height={36} />
                        <Line
                            yAxisId="left"
                            type="monotone"
                            dataKey="revenue"
                            name="revenue"
                            stroke="#0f172a"
                            strokeWidth={2}
                            dot={{ r: 4, fill: "#0f172a" }}
                            activeDot={{ r: 6, strokeWidth: 0 }}
                        />
                        <Line
                            yAxisId="right"
                            type="monotone"
                            dataKey="orders"
                            name="orders"
                            stroke="#3b82f6"
                            strokeWidth={2}
                            dot={{ r: 4, fill: "#3b82f6" }}
                            activeDot={{ r: 6, strokeWidth: 0 }}
                        />
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}
