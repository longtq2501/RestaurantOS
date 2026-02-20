"use client";

import { orderApi } from "@/lib/api/orders";
import { OrderItemStatus, OrderStatus } from "@restaurantos/types";
import {
    Badge,
    Button,
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@restaurantos/ui";
import { formatCurrency, formatDateTime } from "@restaurantos/utils";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { CheckCircle2, Clock, MapPin, Phone, User, XCircle } from "lucide-react";
import { toast } from "sonner";

interface OrderDetailModalProps {
    orderId: string | null;
    onClose: () => void;
}

export function OrderDetailModal({ orderId, onClose }: OrderDetailModalProps) {
    const queryClient = useQueryClient();

    const { data: orderResponse, isLoading } = useQuery({
        queryKey: ["order", orderId],
        queryFn: () => orderApi.getOrderById(orderId!),
        enabled: !!orderId,
    });

    const updateStatusMutation = useMutation({
        mutationFn: ({ id, status }: { id: string; status: OrderStatus }) =>
            orderApi.updateOrderStatus(id, status),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["orders"] });
            queryClient.invalidateQueries({ queryKey: ["order", orderId] });
            toast.success("Order status updated successfully");
        },
        onError: () => {
            toast.error("Failed to update order status");
        },
    });

    const updateItemStatusMutation = useMutation({
        mutationFn: ({ id, status }: { id: string; status: OrderItemStatus }) =>
            orderApi.updateOrderItemStatus(id, status),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["order", orderId] });
            toast.success("Item status updated");
        },
    });

    const order = orderResponse?.data;

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

    const getItemStatusVariant = (status: OrderItemStatus) => {
        switch (status) {
            case OrderItemStatus.PENDING: return "secondary";
            case OrderItemStatus.PREPARING: return "warning";
            case OrderItemStatus.READY: return "default";
            case OrderItemStatus.SERVED: return "success";
            default: return "outline";
        }
    };

    if (!orderId) return null;

    return (
        <Dialog open={!!orderId} onOpenChange={(open) => !open && onClose()}>
            <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <div className="flex items-center justify-between pr-8">
                        <div>
                            <DialogTitle className="text-2xl">Order {order?.orderNumber}</DialogTitle>
                            <DialogDescription>
                                Placed on {order ? formatDateTime(order.createdAt) : "..."}
                            </DialogDescription>
                        </div>
                        {order && (
                            <Badge variant={getStatusVariant(order.status)} className="text-sm px-3 py-1">
                                {order.status}
                            </Badge>
                        )}
                    </div>
                </DialogHeader>

                {isLoading ? (
                    <div className="py-12 text-center text-slate-500">Loading order details...</div>
                ) : !order ? (
                    <div className="py-12 text-center text-slate-500">Order not found.</div>
                ) : (
                    <div className="space-y-8 py-4">
                        {/* Summary Grid */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                            <div className="space-y-1">
                                <p className="text-xs font-semibold text-slate-500 uppercase flex items-center">
                                    <MapPin className="h-3 w-3 mr-1" /> Table
                                </p>
                                <p className="font-medium text-lg">Table {order.tableNumber}</p>
                            </div>
                            <div className="space-y-1">
                                <p className="text-xs font-semibold text-slate-500 uppercase flex items-center">
                                    <User className="h-3 w-3 mr-1" /> Customer
                                </p>
                                <div className="flex items-center gap-2">
                                    <p className="font-medium">{order.customerName || "Walk-in"}</p>
                                    {order.customerPhone && (
                                        <span className="text-slate-400 text-sm flex items-center">
                                            <Phone className="h-3 w-3 mx-1" /> {order.customerPhone}
                                        </span>
                                    )}
                                </div>
                            </div>
                            <div className="space-y-1">
                                <p className="text-xs font-semibold text-slate-500 uppercase flex items-center">
                                    <Clock className="h-3 w-3 mr-1" /> Status
                                </p>
                                <select
                                    className="w-full rounded-md border border-slate-200 bg-white px-3 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20"
                                    value={order.status}
                                    onChange={(e) => updateStatusMutation.mutate({
                                        id: order.id,
                                        status: e.target.value as OrderStatus
                                    })}
                                    disabled={updateStatusMutation.isPending}
                                >
                                    {Object.values(OrderStatus).map((status) => (
                                        <option key={status} value={status}>{status}</option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        {/* Items Table */}
                        <div className="space-y-4">
                            <h3 className="font-semibold text-lg">Order Items</h3>
                            <div className="border rounded-lg overflow-hidden">
                                <Table>
                                    <TableHeader className="bg-slate-50">
                                        <TableRow>
                                            <TableHead>Item</TableHead>
                                            <TableHead className="text-center">Qty</TableHead>
                                            <TableHead className="text-right">Price</TableHead>
                                            <TableHead className="text-right">Subtotal</TableHead>
                                            <TableHead className="text-center">Status</TableHead>
                                        </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                        {order.items.map((item) => (
                                            <TableRow key={item.id}>
                                                <TableCell className="font-medium">
                                                    <div>
                                                        {item.itemName}
                                                        {item.specialInstructions && (
                                                            <p className="text-xs text-orange-600 italic">
                                                                Note: {item.specialInstructions}
                                                            </p>
                                                        )}
                                                    </div>
                                                </TableCell>
                                                <TableCell className="text-center">{item.quantity}</TableCell>
                                                <TableCell className="text-right">{formatCurrency(item.unitPrice)}</TableCell>
                                                <TableCell className="text-right">{formatCurrency(item.subtotal)}</TableCell>
                                                <TableCell className="text-center">
                                                    <select
                                                        className="rounded-md border border-slate-200 bg-white px-2 py-1 text-xs focus:outline-none"
                                                        value={item.status}
                                                        onChange={(e) => updateItemStatusMutation.mutate({
                                                            id: item.id,
                                                            status: e.target.value as OrderItemStatus
                                                        })}
                                                        disabled={updateItemStatusMutation.isPending}
                                                    >
                                                        {Object.values(OrderItemStatus).map((status) => (
                                                            <option key={status} value={status}>{status}</option>
                                                        ))}
                                                    </select>
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </div>
                        </div>

                        {/* Financial Summary */}
                        <div className="flex flex-col md:flex-row justify-between pt-4 border-t gap-6">
                            <div className="space-y-4 flex-1">
                                <div className="p-3 bg-slate-50 rounded-lg space-y-2">
                                    <p className="text-xs font-semibold text-slate-500 uppercase">Special Instructions</p>
                                    <p className="text-sm">{order.specialInstructions || "No special instructions"}</p>
                                </div>
                            </div>
                            <div className="w-full md:w-64 space-y-2">
                                <div className="flex justify-between text-sm">
                                    <span className="text-slate-500">Subtotal</span>
                                    <span>{formatCurrency(order.subtotal)}</span>
                                </div>
                                <div className="flex justify-between text-sm">
                                    <span className="text-slate-500">Tax</span>
                                    <span>{formatCurrency(order.taxAmount)}</span>
                                </div>
                                <div className="flex justify-between text-sm">
                                    <span className="text-slate-500">Discount</span>
                                    <span className="text-danger">-{formatCurrency(order.discountAmount)}</span>
                                </div>
                                <div className="flex justify-between pt-2 border-t font-bold text-lg">
                                    <span>Total</span>
                                    <span>{formatCurrency(order.totalAmount)}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                <DialogFooter className="border-t pt-4">
                    <Button variant="outline" onClick={onClose}>Close</Button>
                    {order?.status !== OrderStatus.COMPLETED && order?.status !== OrderStatus.CANCELLED && (
                        <Button
                            variant="success"
                            onClick={() => updateStatusMutation.mutate({
                                id: order!.id,
                                status: OrderStatus.COMPLETED
                            })}
                            disabled={updateStatusMutation.isPending}
                        >
                            <CheckCircle2 className="h-4 w-4 mr-2" />
                            Mark as Completed
                        </Button>
                    )}
                    {order?.status === OrderStatus.PENDING && (
                        <Button
                            variant="danger"
                            onClick={() => updateStatusMutation.mutate({
                                id: order!.id,
                                status: OrderStatus.CANCELLED
                            })}
                            disabled={updateStatusMutation.isPending}
                        >
                            <XCircle className="h-4 w-4 mr-2" />
                            Cancel Order
                        </Button>
                    )}
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
