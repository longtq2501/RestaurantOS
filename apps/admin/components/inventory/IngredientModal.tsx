"use client";

import { inventoryApi } from "@/lib/api/inventory";
import { useAuthStore } from "@/store/auth.store";
import { zodResolver } from "@hookform/resolvers/zod";
import * as DialogPrimitive from "@radix-ui/react-dialog";
import { Ingredient, IngredientRequest } from "@restaurantos/types";
import { Button } from "@restaurantos/ui";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { X } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import * as z from "zod";

const ingredientSchema = z.object({
    name: z.string().min(1, "Tên nguyên liệu không được để trống"),
    unit: z.string().min(1, "Đơn vị tính không được để trống"),
    currentStock: z.number().min(0, "Tồn kho không được âm"),
    minStock: z.number().min(0, "Định mức tối thiểu không được âm"),
    costPerUnit: z.number().min(0, "Đơn giá không được âm").optional(),
    supplierName: z.string().optional(),
    supplierPhone: z.string().optional(),
});

type IngredientFormValues = z.infer<typeof ingredientSchema>;

interface IngredientModalProps {
    isOpen: boolean;
    onClose: () => void;
    ingredient?: Ingredient;
}

export function IngredientModal({
    isOpen,
    onClose,
    ingredient,
}: IngredientModalProps) {
    const user = useAuthStore((state) => state.user);
    const queryClient = useQueryClient();
    const isEdit = !!ingredient;
    const [mounted, setMounted] = useState(false);

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm<IngredientFormValues>({
        resolver: zodResolver(ingredientSchema),
        defaultValues: {
            name: "",
            unit: "",
            currentStock: 0,
            minStock: 0,
            costPerUnit: 0,
            supplierName: "",
            supplierPhone: "",
        },
    });

    useEffect(() => {
        setMounted(true);
    }, []);

    useEffect(() => {
        if (!isOpen) return;

        if (ingredient) {
            reset({
                name: ingredient.name,
                unit: ingredient.unit,
                currentStock: ingredient.currentStock,
                minStock: ingredient.minStock,
                costPerUnit: ingredient.costPerUnit,
                supplierName: ingredient.supplierName || "",
                supplierPhone: ingredient.supplierPhone || "",
            });
        } else {
            reset({
                name: "",
                unit: "",
                currentStock: 0,
                minStock: 0,
                costPerUnit: 0,
                supplierName: "",
                supplierPhone: "",
            });
        }
    }, [ingredient, reset, isOpen]);

    const mutation = useMutation({
        mutationFn: (data: IngredientFormValues) => {
            if (!user?.restaurantId) throw new Error("Restaurant ID is missing");

            if (isEdit && ingredient) {
                return inventoryApi.updateIngredient(
                    user.restaurantId,
                    ingredient.id,
                    data as IngredientRequest
                );
            } else {
                return inventoryApi.createIngredient(user.restaurantId, data as IngredientRequest);
            }
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["ingredients"] });
            toast.success(isEdit ? "Đã cập nhật nguyên liệu" : "Đã thêm nguyên liệu mới");
            onClose();
        },
        onError: (error: any) => {
            console.error("Mutation error:", error);
            toast.error(error.response?.data?.message || "Đã có lỗi xảy ra khi lưu nguyên liệu");
        },
    });

    const onSubmit = (data: IngredientFormValues) => {
        mutation.mutate(data);
    };

    if (!mounted) return null;

    return (
        <DialogPrimitive.Root
            open={isOpen}
            onOpenChange={(open) => {
                if (!open) onClose();
            }}
        >
            <DialogPrimitive.Portal>
                <DialogPrimitive.Overlay className="fixed inset-0 z-[9999] bg-black/50" />
                <DialogPrimitive.Content className="fixed left-[50%] top-[50%] z-[9999] grid w-full max-w-lg translate-x-[-50%] translate-y-[-50%] gap-4 border bg-white p-6 shadow-lg sm:rounded-lg">
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="flex flex-col space-y-1.5 text-center sm:text-left mb-4">
                            <DialogPrimitive.Title className="text-lg font-semibold">
                                {isEdit ? "Chỉnh sửa nguyên liệu" : "Thêm nguyên liệu mới"}
                            </DialogPrimitive.Title>
                            <DialogPrimitive.Description className="text-sm text-slate-500">
                                {isEdit
                                    ? "Cập nhật thông tin chi tiết cho nguyên liệu này."
                                    : "Thêm một nguyên liệu mới vào kho của bạn."}
                            </DialogPrimitive.Description>
                        </div>

                        <div className="grid gap-4 py-4 max-h-[60vh] overflow-y-auto pr-2">
                            <div className="flex flex-col gap-2">
                                <label htmlFor="name" className="text-sm font-medium">
                                    Tên nguyên liệu
                                </label>
                                <input
                                    id="name"
                                    type="text"
                                    className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                    placeholder="VD: Thịt bò, Hành tây..."
                                    {...register("name")}
                                />
                                {errors.name && (
                                    <p className="text-xs text-danger">{errors.name.message}</p>
                                )}
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div className="flex flex-col gap-2">
                                    <label htmlFor="unit" className="text-sm font-medium">
                                        Đơn vị tính
                                    </label>
                                    <input
                                        id="unit"
                                        type="text"
                                        className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                        placeholder="VD: kg, gói, chai..."
                                        {...register("unit")}
                                    />
                                    {errors.unit && (
                                        <p className="text-xs text-danger">{errors.unit.message}</p>
                                    )}
                                </div>
                                <div className="flex flex-col gap-2">
                                    <label htmlFor="costPerUnit" className="text-sm font-medium">
                                        Đơn giá (VNĐ)
                                    </label>
                                    <input
                                        id="costPerUnit"
                                        type="number"
                                        step="0.01"
                                        className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                        {...register("costPerUnit", { valueAsNumber: true })}
                                    />
                                    {errors.costPerUnit && (
                                        <p className="text-xs text-danger">
                                            {errors.costPerUnit.message}
                                        </p>
                                    )}
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div className="flex flex-col gap-2">
                                    <label htmlFor="currentStock" className="text-sm font-medium">
                                        Tồn kho hiện tại
                                    </label>
                                    <input
                                        id="currentStock"
                                        type="number"
                                        step="0.01"
                                        className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                        disabled={isEdit}
                                        {...register("currentStock", { valueAsNumber: true })}
                                    />
                                    {errors.currentStock && (
                                        <p className="text-xs text-danger">
                                            {errors.currentStock.message}
                                        </p>
                                    )}
                                </div>
                                <div className="flex flex-col gap-2">
                                    <label htmlFor="minStock" className="text-sm font-medium">
                                        Định mức tối thiểu
                                    </label>
                                    <input
                                        id="minStock"
                                        type="number"
                                        step="0.01"
                                        className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                        {...register("minStock", { valueAsNumber: true })}
                                    />
                                    {errors.minStock && (
                                        <p className="text-xs text-danger">
                                            {errors.minStock.message}
                                        </p>
                                    )}
                                </div>
                            </div>

                            <div className="flex flex-col gap-2">
                                <label htmlFor="supplierName" className="text-sm font-medium">
                                    Nhà cung cấp
                                </label>
                                <input
                                    id="supplierName"
                                    type="text"
                                    className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                    {...register("supplierName")}
                                />
                            </div>

                            <div className="flex flex-col gap-2">
                                <label htmlFor="supplierPhone" className="text-sm font-medium">
                                    Số điện thoại nhà cung cấp
                                </label>
                                <input
                                    id="supplierPhone"
                                    type="text"
                                    className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                    {...register("supplierPhone")}
                                />
                            </div>
                        </div>

                        <div className="flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2 mt-4">
                            <Button type="button" variant="outline" onClick={onClose}>
                                Hủy
                            </Button>
                            <Button type="submit" disabled={mutation.isPending}>
                                {mutation.isPending ? "Đang lưu..." : "Lưu thay đổi"}
                            </Button>
                        </div>
                    </form>
                    <DialogPrimitive.Close className="absolute right-4 top-4 rounded-sm opacity-70 hover:opacity-100">
                        <X className="h-4 w-4" />
                        <span className="sr-only">Close</span>
                    </DialogPrimitive.Close>
                </DialogPrimitive.Content>
            </DialogPrimitive.Portal>
        </DialogPrimitive.Root>
    );
}
