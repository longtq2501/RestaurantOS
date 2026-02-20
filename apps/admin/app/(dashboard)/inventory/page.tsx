"use client";

import { AdjustmentModal } from "@/components/inventory/AdjustmentModal";
import { IngredientModal } from "@/components/inventory/IngredientModal";
import { inventoryApi } from "@/lib/api/inventory";
import { useAuthStore } from "@/store/auth.store";
import { Ingredient } from "@restaurantos/types";
import { Badge, Button } from "@restaurantos/ui";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    AlertTriangle,
    Edit,
    History,
    Plus,
    Search,
    Trash2
} from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";

export default function InventoryPage() {
    const user = useAuthStore((state) => state.user);
    const queryClient = useQueryClient();
    const [searchTerm, setSearchTerm] = useState("");
    const [isIngredientModalOpen, setIsIngredientModalOpen] = useState(false);
    const [isAdjustmentModalOpen, setIsAdjustmentModalOpen] = useState(false);
    const [selectedIngredient, setSelectedIngredient] = useState<Ingredient | null>(
        null
    );
    const [activeTab, setActiveTab] = useState<"ingredients" | "recipes">(
        "ingredients"
    );

    const { data: ingredients = [], isLoading } = useQuery({
        queryKey: ["ingredients", user?.restaurantId],
        queryFn: () => (user?.restaurantId ? inventoryApi.getAll(user.restaurantId) : []),
        enabled: !!user?.restaurantId,
        select: (res: any) => res.data as Ingredient[],
    });

    const deleteMutation = useMutation({
        mutationFn: (id: string) => {
            if (!user?.restaurantId) throw new Error("Restaurant ID is missing");
            return inventoryApi.deleteIngredient(user.restaurantId, id);
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["ingredients"] });
            toast.success("Đã xóa nguyên liệu");
        },
        onError: (error: any) => {
            toast.error(error.response?.data?.message || "Không thể xóa nguyên liệu");
        },
    });

    const filteredIngredients = ingredients.filter((item) =>
        item.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleEdit = (ingredient: Ingredient) => {
        setSelectedIngredient(ingredient);
        setIsIngredientModalOpen(true);
    };

    const handleAdjust = (ingredient: Ingredient) => {
        setSelectedIngredient(ingredient);
        setIsAdjustmentModalOpen(true);
    };

    const handleDelete = (id: string) => {
        if (confirm("Bạn có chắc chắn muốn xóa nguyên liệu này?")) {
            deleteMutation.mutate(id);
        }
    };

    const openAddModal = () => {
        setSelectedIngredient(null);
        setIsIngredientModalOpen(true);
    };

    return (
        <div className="space-y-6">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                <div>
                    <h1 className="text-2xl font-bold">Quản lý kho</h1>
                    <p className="text-slate-500">
                        Theo dõi nguyên liệu và mức độ tồn kho trong nhà hàng.
                    </p>
                </div>
                <Button className="w-fit" onClick={openAddModal}>
                    <Plus className="mr-2 h-4 w-4" />
                    Thêm nguyên liệu
                </Button>
            </div>

            <div className="flex border-b border-slate-200">
                <button
                    className={`px-4 py-2 text-sm font-medium border-b-2 transition-colors ${activeTab === "ingredients"
                            ? "border-primary text-primary"
                            : "border-transparent text-slate-500 hover:text-slate-700"
                        }`}
                    onClick={() => setActiveTab("ingredients")}
                >
                    Nguyên liệu
                </button>
                <button
                    className={`px-4 py-2 text-sm font-medium border-b-2 transition-colors ${activeTab === "recipes"
                            ? "border-primary text-primary"
                            : "border-transparent text-slate-500 hover:text-slate-700"
                        }`}
                    onClick={() => setActiveTab("recipes")}
                >
                    Công thức món ăn
                </button>
            </div>

            {activeTab === "ingredients" ? (
                <>
                    <div className="flex items-center gap-4 rounded-lg bg-white p-4 shadow-sm border">
                        <div className="relative flex-1">
                            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                            <input
                                type="text"
                                placeholder="Tìm kiếm nguyên liệu..."
                                className="w-full rounded-md border border-slate-200 pl-10 pr-4 py-2 text-sm outline-none focus:ring-2 focus:ring-primary/20"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>
                    </div>

                    <div className="rounded-lg bg-white shadow-sm border overflow-hidden">
                        <div className="overflow-x-auto">
                            <table className="w-full text-left text-sm">
                                <thead className="bg-slate-50 border-b">
                                    <tr>
                                        <th className="px-6 py-4 font-semibold">Tên nguyên liệu</th>
                                        <th className="px-6 py-4 font-semibold text-center">Tồn hiện tại</th>
                                        <th className="px-6 py-4 font-semibold text-center">Định mức</th>
                                        <th className="px-6 py-4 font-semibold text-center">Trạng thái</th>
                                        <th className="px-6 py-4 font-semibold">Nhà cung cấp</th>
                                        <th className="px-6 py-4 font-semibold text-right">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y">
                                    {isLoading ? (
                                        <tr>
                                            <td colSpan={6} className="px-6 py-10 text-center text-slate-500">
                                                Đang tải dữ liệu...
                                            </td>
                                        </tr>
                                    ) : filteredIngredients.length === 0 ? (
                                        <tr>
                                            <td colSpan={6} className="px-6 py-10 text-center text-slate-500">
                                                {searchTerm ? "Không tìm thấy nguyên liệu nào." : "Chưa có nguyên liệu nào trong kho."}
                                            </td>
                                        </tr>
                                    ) : (
                                        filteredIngredients.map((item) => {
                                            const isLowStock = item.currentStock <= item.minStock;
                                            return (
                                                <tr key={item.id} className="hover:bg-slate-50 transition-colors">
                                                    <td className="px-6 py-4">
                                                        <div className="font-medium text-slate-900">{item.name}</div>
                                                        <div className="text-xs text-slate-500">
                                                            {item.costPerUnit?.toLocaleString("vi-VN")}đ / {item.unit}
                                                        </div>
                                                    </td>
                                                    <td className="px-6 py-4 text-center">
                                                        <span className={`font-semibold ${isLowStock ? "text-danger" : "text-green-600"}`}>
                                                            {item.currentStock} {item.unit}
                                                        </span>
                                                    </td>
                                                    <td className="px-6 py-4 text-center text-slate-500">
                                                        {item.minStock} {item.unit}
                                                    </td>
                                                    <td className="px-6 py-4 text-center">
                                                        {isLowStock ? (
                                                            <Badge variant="destructive" className="flex items-center gap-1">
                                                                <AlertTriangle className="h-3 w-3" />
                                                                Sắp hết
                                                            </Badge>
                                                        ) : (
                                                            <Badge variant="outline" className="text-green-600 border-green-200 bg-green-50">
                                                                Bình thường
                                                            </Badge>
                                                        )}
                                                    </td>
                                                    <td className="px-6 py-4">
                                                        <div className="text-slate-900">{item.supplierName || "-"}</div>
                                                        <div className="text-xs text-slate-500">{item.supplierPhone}</div>
                                                    </td>
                                                    <td className="px-6 py-4 text-right">
                                                        <div className="flex justify-end gap-2">
                                                            <Button
                                                                variant="outline"
                                                                size="sm"
                                                                className="px-2"
                                                                title="Điều chỉnh kho"
                                                                onClick={() => handleAdjust(item)}
                                                            >
                                                                <History className="h-4 w-4" />
                                                            </Button>
                                                            <Button
                                                                variant="outline"
                                                                size="sm"
                                                                className="px-2"
                                                                title="Chỉnh sửa"
                                                                onClick={() => handleEdit(item)}
                                                            >
                                                                <Edit className="h-4 w-4" />
                                                            </Button>
                                                            <Button
                                                                variant="outline"
                                                                size="sm"
                                                                className="px-2 text-danger hover:bg-danger/5"
                                                                title="Xóa"
                                                                onClick={() => handleDelete(item.id)}
                                                            >
                                                                <Trash2 className="h-4 w-4" />
                                                            </Button>
                                                        </div>
                                                    </td>
                                                </tr>
                                            );
                                        })
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </>
            ) : (
                <div className="rounded-lg bg-white p-8 text-center shadow-sm border">
                    <History className="h-12 w-12 mx-auto text-slate-300 mb-4" />
                    <h3 className="text-lg font-semibold mb-2">Quản lý công thức (Đang phát triển)</h3>
                    <p className="text-slate-500 max-w-md mx-auto">
                        Tính năng liên kết nguyên liệu vào từng món ăn để tự động hạch toán kho sẽ sớm ra mắt.
                    </p>
                </div>
            )}

            <IngredientModal
                isOpen={isIngredientModalOpen}
                onClose={() => setIsIngredientModalOpen(false)}
                ingredient={selectedIngredient || undefined}
            />

            <AdjustmentModal
                isOpen={isAdjustmentModalOpen}
                onClose={() => setIsAdjustmentModalOpen(false)}
                ingredient={selectedIngredient}
            />
        </div>
    );
}
