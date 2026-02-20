"use client";

import { tableApi } from "@/lib/api/tables";
import { useAuthStore } from "@/store/auth.store";
import { zodResolver } from "@hookform/resolvers/zod";
import * as DialogPrimitive from "@radix-ui/react-dialog";
import { CreateTableRequest, Table, TableStatus, UpdateTableRequest } from "@restaurantos/types";
import { Button } from "@restaurantos/ui";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { X } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import * as z from "zod";

const tableSchema = z.object({
    tableNumber: z.number().min(1, "Số bàn phải lớn hơn 0"),
    capacity: z.number().min(1, "Sức chứa phải ít nhất 1 người"),
    section: z.string().optional(),
    status: z.nativeEnum(TableStatus).optional(),
});

type TableFormValues = z.infer<typeof tableSchema>;

interface TableModalProps {
    isOpen: boolean;
    onClose: () => void;
    table?: Table;
}

export function TableModal({ isOpen, onClose, table }: TableModalProps) {
    const user = useAuthStore((state) => state.user);
    const queryClient = useQueryClient();
    const isEdit = !!table;
    const [mounted, setMounted] = useState(false);

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm<TableFormValues>({
        resolver: zodResolver(tableSchema),
        defaultValues: {
            tableNumber: 1,
            capacity: 2,
            section: "Sảnh chính",
            status: TableStatus.EMPTY,
        },
    });

    useEffect(() => {
        setMounted(true);
    }, []);

    useEffect(() => {
        if (!isOpen) return;

        if (table) {
            reset({
                tableNumber: table.tableNumber,
                capacity: table.capacity,
                section: table.section || "",
                status: table.status,
            });
        } else {
            reset({
                tableNumber: 1,
                capacity: 2,
                section: "Sảnh chính",
                status: TableStatus.EMPTY,
            });
        }
    }, [table, reset, isOpen, isEdit]);

    const mutation = useMutation({
        mutationFn: (data: TableFormValues) => {
            if (!user?.restaurantId) throw new Error("Restaurant ID is missing");

            if (isEdit && table) {
                return tableApi.updateTable(user.restaurantId, table.id, data as UpdateTableRequest);
            } else {
                return tableApi.createTable(user.restaurantId, data as CreateTableRequest);
            }
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["tables"] });
            toast.success(isEdit ? "Đã cập nhật bàn" : "Đã thêm bàn mới");
            onClose();
        },
        onError: (error) => {
            console.error("Mutation error:", error);
            toast.error("Đã có lỗi xảy ra khi lưu bàn");
        },
    });

    const onSubmit = (data: TableFormValues) => {
        mutation.mutate(data);
    };

    if (!mounted) return null;

    return (
        <DialogPrimitive.Root open={isOpen} onOpenChange={(open) => { if (!open) onClose(); }}>
            <DialogPrimitive.Portal>
                <DialogPrimitive.Overlay className="fixed inset-0 z-[9999] bg-black/50" />
                <DialogPrimitive.Content className="fixed left-[50%] top-[50%] z-[9999] grid w-full max-w-lg translate-x-[-50%] translate-y-[-50%] gap-4 border bg-white p-6 shadow-lg sm:rounded-lg">
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="flex flex-col space-y-1.5 text-center sm:text-left mb-4">
                            <DialogPrimitive.Title className="text-lg font-semibold">
                                {isEdit ? "Chỉnh sửa bàn" : "Thêm bàn mới"}
                            </DialogPrimitive.Title>
                            <DialogPrimitive.Description className="text-sm text-slate-500">
                                {isEdit ? "Cập nhật thông tin chi tiết cho bàn này." : "Thêm một bàn mới vào sơ đồ nhà hàng của bạn."}
                            </DialogPrimitive.Description>
                        </div>

                        <div className="grid gap-4 py-4">
                            <div className="flex flex-col gap-2">
                                <label htmlFor="tableNumber" className="text-sm font-medium">Số bàn</label>
                                <input
                                    id="tableNumber"
                                    type="number"
                                    className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                    {...register("tableNumber", { valueAsNumber: true })}
                                />
                                {errors.tableNumber && <p className="text-xs text-danger">{errors.tableNumber.message}</p>}
                            </div>

                            <div className="flex flex-col gap-2">
                                <label htmlFor="capacity" className="text-sm font-medium">Sức chứa (người)</label>
                                <input
                                    id="capacity"
                                    type="number"
                                    className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                    {...register("capacity", { valueAsNumber: true })}
                                />
                                {errors.capacity && <p className="text-xs text-danger">{errors.capacity.message}</p>}
                            </div>

                            <div className="flex flex-col gap-2">
                                <label htmlFor="section" className="text-sm font-medium">Khu vực</label>
                                <input
                                    id="section"
                                    type="text"
                                    className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                    placeholder="VD: Sảnh chính, Sân thượng..."
                                    {...register("section")}
                                />
                            </div>

                            {isEdit && (
                                <div className="flex flex-col gap-2">
                                    <label htmlFor="status" className="text-sm font-medium">Trạng thái</label>
                                    <select
                                        id="status"
                                        className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm"
                                        {...register("status")}
                                    >
                                        {Object.values(TableStatus).map((status) => (
                                            <option key={status} value={status}>{status}</option>
                                        ))}
                                    </select>
                                </div>
                            )}
                        </div>

                        <div className="flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2 mt-4">
                            <Button type="button" variant="outline" onClick={onClose}>Hủy</Button>
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
