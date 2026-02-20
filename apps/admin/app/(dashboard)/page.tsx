"use client";

import { StatsCard } from "@/components/shared/StatsCard";
import {
    Card,
    CardContent,
    CardHeader,
    CardTitle
} from "@restaurantos/ui";
import {
    DollarSign,
    ShoppingBag,
    TrendingUp,
    Users
} from "lucide-react";
import {
    CartesianGrid,
    Line,
    LineChart,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis
} from "recharts";

const data = [
    { name: "Mon", revenue: 4000, orders: 24 },
    { name: "Tue", revenue: 3000, orders: 18 },
    { name: "Wed", revenue: 2000, orders: 20 },
    { name: "Thu", revenue: 2780, orders: 25 },
    { name: "Fri", revenue: 1890, orders: 15 },
    { name: "Sat", revenue: 2390, orders: 30 },
    { name: "Sun", revenue: 3490, orders: 35 },
];

export default function DashboardPage() {
    return (
        <div className="space-y-8">
            <div>
                <h2 className="text-3xl font-bold tracking-tight">Dashboard</h2>
                <p className="text-slate-500">Welcome to your restaurant overview.</p>
            </div>

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <StatsCard
                    title="Total Revenue"
                    value="₫12,450,000"
                    icon={DollarSign}
                    trend={{ value: 12, isPositive: true }}
                    description="vs last week"
                />
                <StatsCard
                    title="Total Orders"
                    value="+573"
                    icon={ShoppingBag}
                    trend={{ value: 8, isPositive: true }}
                    description="vs last week"
                />
                <StatsCard
                    title="Active Tables"
                    value="12 / 20"
                    icon={Users}
                    description="Currently occupied"
                />
                <StatsCard
                    title="Avg. Order Value"
                    value="₫215,000"
                    icon={TrendingUp}
                    trend={{ value: 4, isPositive: false }}
                    description="vs last week"
                />
            </div>

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
                <Card className="col-span-4">
                    <CardHeader>
                        <CardTitle>Revenue Overview</CardTitle>
                    </CardHeader>
                    <CardContent className="h-[350px]">
                        <ResponsiveContainer width="100%" height="100%">
                            <LineChart data={data}>
                                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                                <XAxis
                                    dataKey="name"
                                    stroke="#64748b"
                                    fontSize={12}
                                    tickLine={false}
                                    axisLine={false}
                                />
                                <YAxis
                                    stroke="#64748b"
                                    fontSize={12}
                                    tickLine={false}
                                    axisLine={false}
                                    tickFormatter={(value) => `₫${value / 1000}k`}
                                />
                                <Tooltip />
                                <Line
                                    type="monotone"
                                    dataKey="revenue"
                                    stroke="#F97316"
                                    strokeWidth={2}
                                    dot={false}
                                />
                            </LineChart>
                        </ResponsiveContainer>
                    </CardContent>
                </Card>

                <Card className="col-span-3">
                    <CardHeader>
                        <CardTitle>Recent Orders</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-8">
                            {[1, 2, 3, 4, 5].map((i) => (
                                <div key={i} className="flex items-center">
                                    <div className="ml-4 space-y-1">
                                        <p className="text-sm font-medium leading-none">Order #ORD-123{i}</p>
                                        <p className="text-xs text-slate-500">
                                            Table {i + 1} • {i * 2} items
                                        </p>
                                    </div>
                                    <div className="ml-auto font-medium">
                                        +₫{(i + 1) * 50000}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
