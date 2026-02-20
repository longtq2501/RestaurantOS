"use client";

import { inventoryApi } from "@/lib/api/inventory";
import { useAuthStore } from "@/store/auth.store";
import { zodResolver } from "@hookform/resolvers/zod";
import * as DialogPrimitive from "@radix-ui/react-dialog";
import { AdjustmentType, Ingredient, StockAdjustmentRequest } from "@restaurantos/types";
import { Button } from "@restaurantos/ui";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { X } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import * as z from "zod";

const adjustmentSchema = z.object({
    quantity: z.number().min(0.0001, "Số lượng phải lớn hơn 0"),
    type: z.nativeEnum(AdjustmentType),
    reason: z.string().optional(),
});

type AdjustmentFormValues = z.infer<typeof adjustmentSchema>;

interface AdjustmentModalProps {
    isOpen: boolean;
    onClose: () => void;
    ingredient: Ingredient | null;
}

export function AdjustmentModal({
    isOpen,
    onClose,
    ingredient,
}: AdjustmentModalProps) {
    const user = useAuthStore((state) => state.user);
    const queryClient = useQueryClient();
    const [mounted, setMounted] = useState(false);

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm<AdjustmentFormValues>({
        resolver: zodResolver(adjustmentSchema),
        defaultValues: {
            quantity: 0,
            type: AdjustmentType.RESTOCK,
            reason: "",
        },
    });

    useEffect(() => {
        setMounted(true);
    }, []);

    useEffect(() => {
        if (isOpen) {
            reset({
                quantity: 0,
                type: AdjustmentType.RESTOCK,
                reason: "",
            });
        }
    }, [isOpen, reset]);

    const mutation = useMutation({
        mutationFn: (data: AdjustmentFormValues) => {
            if (!user?.restaurantId) throw new Error("Restaurant ID is missing");
            if (!ingredient) throw new Error("Ingredient is missing");

            return inventoryApi.adjustStock(
                user.restaurantId,
                ingredient.id,
                data as StockAdjustmentRequest
            );
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["ingredients"] });
            toast.success("Đã điều chỉnh kho thành công");
            onClose();
        },
        onError: (error: any) => {
            console.error("Mutation error:", error);
            toast.error(error.response?.data?.message || "Đã có lỗi xảy ra khi điều chỉnh kho");
        },
    });

    const onSubmit = (data: AdjustmentFormValues) => {
        mutation.mutate(data);
    };

    if (!mounted || !ingredient) return null;

    return (
        <DialogPrimitive.Root
            open={isOpen}
            onOpenChange={(open) => {
                if (!open) onClose();
            }}
        >
            <DialogPrimitive.Portal>
                <DialogPrimitive.Overlay className="fixed inset-0 z-[9999] bg-black/50" />
                <DialogPrimitive.Content className="fixed left-[50%] top-[50%] z-[9999] grid w-full max-w-md translate-x-[-50%] translate-y-[-50%] gap-4 border bg-white p-6 shadow-lg sm:rounded-lg">
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="flex flex-col space-y-1.5 text-center sm:text-left mb-4">
                            <DialogPrimitive.Title className="text-lg font-semibold">
                                Điều chỉnh kho: {ingredient.name}
                            </DialogPrimitive.Title>
                            <DialogPrimitive.Description className="text-sm text-slate-500">
                                Lượng tồn hiện tại: {ingredient.currentStock} {ingredient.unit}
                            </DialogPrimitive.Description>
                        </div>

                        <div className="grid gap-4 py-4">
                            <div className="flex flex-col gap-2">
                                <label htmlFor="type" className="text-sm font-medium">
                                    Loại điều chỉnh
                                </label>
                                <select
                                    id="type"
                                    className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                    {...register("type")}
                                >
                                    <option value={AdjustmentType.RESTOCK}>Nhập thêm (RESTOCK)</option>
                                    <option value={AdjustmentType.WASTE}>Hao hụt/Hỏng (WASTE)</option>
                                    <option value={AdjustmentType.CORRECTION}>Kiểm kê/Sửa đổi (CORRECTION)</option>
                                    <option value={AdjustmentType.MANUAL}>Điều chỉnh thủ công (MANUAL)</option>
                                </select>
                                {errors.type && (
                                    <p className="text-xs text-danger">{errors.type.message}</p>
                                )}
                            </div>

                            <div className="flex flex-col gap-2">
                                <label htmlFor="quantity" className="text-sm font-medium">
                                    Số lượng ({ingredient.unit})
                                </label>
                                <input
                                    id="quantity"
                                    type="number"
                                    step="0.01"
                                    className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                    placeholder="Nhập số lượng..."
                                    {...register("quantity", { valueAsNumber: true })}
                                />
                                {errors.quantity && (
                                    <p className="text-xs text-danger">{errors.quantity.message}</p>
                                )}
                            </div>

                            <div className="flex flex-col gap-2">
                                <label htmlFor="reason" className="text-sm font-medium">
                                    Lý do (Tùy chọn)
                                </label>
                                <textarea
                                    id="reason"
                                    className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                    rows={3}
                                    placeholder="Ghi chú lý do điều chỉnh..."
                                    {...register("reason")}
                                />
                            </div>
                        </div>

                        <div className="flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2 mt-4">
                            <Button type="button" variant="outline" onClick={onClose}>
                                Hủy
                            </Button>
                            <Button type="submit" disabled={mutation.isPending}>
                                {mutation.isPending ? "Đang xử lý..." : "Xác nhận điều chỉnh"}
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
