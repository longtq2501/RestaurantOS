"use client";

import { restaurantApi } from "@/lib/api/restaurant";
import { zodResolver } from "@hookform/resolvers/zod";
import { Restaurant, RestaurantUpdateRequest } from "@restaurantos/types";
import { Button } from "@restaurantos/ui";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import * as z from "zod";

const profileSchema = z.object({
    name: z.string().min(2, "Tên nhà hàng phải có ít nhất 2 ký tự"),
    address: z.string().optional(),
    phone: z.string().optional(),
    email: z.string().email("Email không hợp lệ").optional().or(z.literal("")),
    themeColor: z.string().optional(),
});

type ProfileFormValues = z.infer<typeof profileSchema>;

interface RestaurantProfileFormProps {
    restaurant: Restaurant;
}

export function RestaurantProfileForm({ restaurant }: RestaurantProfileFormProps) {
    const queryClient = useQueryClient();

    const {
        register,
        handleSubmit,
        formState: { errors, isDirty },
        reset,
    } = useForm<ProfileFormValues>({
        resolver: zodResolver(profileSchema),
        defaultValues: {
            name: restaurant.name || "",
            address: restaurant.address || "",
            phone: restaurant.phone || "",
            email: restaurant.email || "",
            themeColor: restaurant.themeColor || "#0f172a",
        },
    });

    const mutation = useMutation({
        mutationFn: (data: RestaurantUpdateRequest) =>
            restaurantApi.updateProfile(restaurant.id, data),
        onSuccess: () => {
            toast.success("Cập nhật thông tin thành công");
            queryClient.invalidateQueries({ queryKey: ["restaurant-profile"] });
            reset(undefined, { keepValues: true });
        },
        onError: (error: any) => {
            toast.error(error.response?.data?.message || "Có lỗi xảy ra khi cập nhật");
        },
    });

    const onSubmit = (data: ProfileFormValues) => {
        mutation.mutate(data);
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6 max-w-2xl">
            <div className="grid gap-4">
                <div className="space-y-2">
                    <label className="text-sm font-medium text-slate-700">Tên nhà hàng</label>
                    <input
                        {...register("name")}
                        className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm focus:border-primary focus:outline-none"
                        placeholder="VD: Nhà hàng Sen Việt"
                    />
                    {errors.name && (
                        <p className="text-xs text-danger">{errors.name.message}</p>
                    )}
                </div>

                <div className="space-y-2">
                    <label className="text-sm font-medium text-slate-700">Địa chỉ</label>
                    <input
                        {...register("address")}
                        className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm focus:border-primary focus:outline-none"
                        placeholder="Số 1, Đường 2, Quận 3..."
                    />
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                    <div className="space-y-2">
                        <label className="text-sm font-medium text-slate-700">Số điện thoại</label>
                        <input
                            {...register("phone")}
                            className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm focus:border-primary focus:outline-none"
                            placeholder="0123 456 789"
                        />
                    </div>
                    <div className="space-y-2">
                        <label className="text-sm font-medium text-slate-700">Email liên hệ</label>
                        <input
                            {...register("email")}
                            className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm focus:border-primary focus:outline-none"
                            placeholder="contact@restaurant.com"
                        />
                        {errors.email && (
                            <p className="text-xs text-danger">{errors.email.message}</p>
                        )}
                    </div>
                </div>

                <div className="space-y-2">
                    <label className="text-sm font-medium text-slate-700">Màu chủ đạo (Theme)</label>
                    <div className="flex items-center gap-3">
                        <input
                            type="color"
                            {...register("themeColor")}
                            className="h-10 w-20 rounded border border-slate-200 p-0.5"
                        />
                        <span className="text-sm text-slate-500">
                            Màu sắc hiển thị trên Menu dành cho khách hàng
                        </span>
                    </div>
                </div>
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t">
                <Button
                    type="button"
                    variant="outline"
                    onClick={() => reset()}
                    disabled={!isDirty || mutation.isPending}
                >
                    Hủy
                </Button>
                <Button type="submit" disabled={!isDirty || mutation.isPending}>
                    {mutation.isPending ? "Đang lưu..." : "Lưu thay đổi"}
                </Button>
            </div>
        </form>
    );
}
