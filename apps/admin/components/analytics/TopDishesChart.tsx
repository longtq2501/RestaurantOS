"use client";

import { TopDish } from "@restaurantos/types";
import {
    Bar,
    BarChart,
    CartesianGrid,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from "recharts";

interface TopDishesChartProps {
    data: TopDish[];
    isLoading: boolean;
}

export function TopDishesChart({ data, isLoading }: TopDishesChartProps) {
    if (isLoading) {
        return (
            <div className="flex h-[400px] w-full items-center justify-center rounded-xl border bg-white border-slate-100">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        );
    }

    return (
        <div className="rounded-xl border bg-white p-6 shadow-sm">
            <div className="mb-6">
                <h3 className="text-lg font-semibold">Top món ăn bán chạy</h3>
                <p className="text-sm text-slate-500">
                    Những món ăn mang lại doanh thu cao nhất cho nhà hàng
                </p>
            </div>
            <div className="h-[350px] w-full">
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={data} layout="vertical" margin={{ left: 40, right: 40 }}>
                        <CartesianGrid strokeDasharray="3 3" horizontal={true} vertical={false} stroke="#f1f5f9" />
                        <XAxis type="number" hide />
                        <YAxis
                            dataKey="menuItemName"
                            type="category"
                            axisLine={false}
                            tickLine={false}
                            tick={{ fill: "#64748b", fontSize: 12 }}
                            width={100}
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
                                if (name === "quantity") return [value + " đĩa", "Số lượng"];
                                return [value, name];
                            }}
                        />
                        <Bar
                            dataKey="revenue"
                            fill="#0f172a"
                            radius={[0, 4, 4, 0]}
                            barSize={20}
                            name="revenue"
                        />
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}
