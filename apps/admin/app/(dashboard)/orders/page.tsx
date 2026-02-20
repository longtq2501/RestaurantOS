"use client";

import { OrderDetailModal } from "@/components/orders/OrderDetailModal";
import { orderApi } from "@/lib/api/orders";
import { useAuthStore } from "@/store/auth.store";
import { OrderStatus } from "@restaurantos/types";
import {
    Badge,
    Button,
    Card,
    CardContent,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow
} from "@restaurantos/ui";
import { formatCurrency, formatDateTime } from "@restaurantos/utils";
import { useQuery } from "@tanstack/react-query";
import { Eye, RefreshCw, Search } from "lucide-react";
import { useState } from "react";

const statusTabs: { label: string; value: OrderStatus | "ALL" }[] = [
    { label: "All Orders", value: "ALL" },
    { label: "Pending", value: OrderStatus.PENDING },
    { label: "Preparing", value: OrderStatus.PREPARING },
    { label: "Ready", value: OrderStatus.READY },
    { label: "Completed", value: OrderStatus.COMPLETED },
    { label: "Cancelled", value: OrderStatus.CANCELLED },
];

const getStatusVariant = (status: OrderStatus) => {
    switch (status) {
        case OrderStatus.PENDING: return "secondary";
        case OrderStatus.PREPARING: return "warning";
        case OrderStatus.READY: return "default";
        case OrderStatus.COMPLETED: return "success";
        case OrderStatus.CANCELLED: return "destructive";
        default: return "outline";
    }
};

export default function OrdersPage() {
    const user = useAuthStore((state) => state.user);
    const [activeTab, setActiveTab] = useState<OrderStatus | "ALL">("ALL");
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedOrderId, setSelectedOrderId] = useState<string | null>(null);

    const { data: ordersResponse, isLoading, refetch } = useQuery({
        queryKey: ["orders", user?.restaurantId, activeTab],
        queryFn: () => orderApi.getOrders(
            user?.restaurantId || "",
            activeTab === "ALL" ? undefined : activeTab
        ),
        enabled: !!user?.restaurantId,
    });

    const orders = ordersResponse?.data || [];

    const filteredOrders = orders.filter(order =>
        order.orderNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
        order.customerName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        order.tableNumber.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Orders</h1>
                    <p className="text-slate-500">Manage and track restaurant orders in real-time.</p>
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" size="sm" onClick={() => refetch()} disabled={isLoading}>
                        <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? "animate-spin" : ""}`} />
                        Refresh
                    </Button>
                </div>
            </div>

            <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div className="flex items-center gap-1 overflow-x-auto pb-2 md:pb-0">
                    {statusTabs.map((tab) => (
                        <button
                            key={tab.value}
                            onClick={() => setActiveTab(tab.value)}
                            className={`px-4 py-2 text-sm font-medium rounded-md whitespace-nowrap transition-colors ${activeTab === tab.value
                                ? "bg-primary text-white"
                                : "text-slate-600 hover:bg-slate-100"
                                }`}
                        >
                            {tab.label}
                        </button>
                    ))}
                </div>
                <div className="relative w-full md:w-72">
                    <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search orders..."
                        className="w-full rounded-md border border-slate-200 pl-10 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
            </div>

            <Card>
                <CardContent className="p-0">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead className="w-[100px]">Order #</TableHead>
                                <TableHead>Date & Time</TableHead>
                                <TableHead>Table</TableHead>
                                <TableHead>Customer</TableHead>
                                <TableHead>Total</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead className="text-right">Actions</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {isLoading ? (
                                <TableRow>
                                    <TableCell colSpan={7} className="h-24 text-center">
                                        Loading orders...
                                    </TableCell>
                                </TableRow>
                            ) : filteredOrders.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={7} className="h-24 text-center">
                                        No orders found.
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filteredOrders.map((order) => (
                                    <TableRow key={order.id}>
                                        <TableCell className="font-medium">{order.orderNumber}</TableCell>
                                        <TableCell className="text-slate-500">
                                            {formatDateTime(order.createdAt)}
                                        </TableCell>
                                        <TableCell>Table {order.tableNumber}</TableCell>
                                        <TableCell>{order.customerName || "Walk-in"}</TableCell>
                                        <TableCell className="font-medium">
                                            {formatCurrency(order.totalAmount)}
                                        </TableCell>
                                        <TableCell>
                                            <Badge variant={getStatusVariant(order.status)}>
                                                {order.status}
                                            </Badge>
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                onClick={() => setSelectedOrderId(order.id)}
                                            >
                                                <Eye className="h-4 w-4 mr-1" />
                                                Details
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            <OrderDetailModal
                orderId={selectedOrderId}
                onClose={() => setSelectedOrderId(null)}
            />
        </div>
    );
}
