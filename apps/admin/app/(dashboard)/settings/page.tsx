"use client";

import { LogoUpload } from "@/components/settings/LogoUpload";
import { RestaurantProfileForm } from "@/components/settings/RestaurantProfileForm";
import { restaurantApi } from "@/lib/api/restaurant";
import { useAuthStore } from "@/store/auth.store";
import { useQuery } from "@tanstack/react-query";
import { Palette, Settings, Shield, User } from "lucide-react";

export default function SettingsPage() {
    const user = useAuthStore((state) => state.user);

    const { data: restaurant, isLoading } = useQuery({
        queryKey: ["restaurant-profile", user?.restaurantId],
        queryFn: () => (user?.restaurantId ? restaurantApi.getProfile(user.restaurantId) : null),
        enabled: !!user?.restaurantId,
        select: (res: any) => res.data,
    });

    if (isLoading) {
        return (
            <div className="flex h-[400px] w-full items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        );
    }

    if (!restaurant) {
        return (
            <div className="p-8 text-center bg-white rounded-xl border border-slate-100 shadow-sm">
                <Settings className="h-12 w-12 text-slate-300 mx-auto mb-4" />
                <h3 className="text-lg font-semibold text-slate-900">Không tìm thấy thông tin nhà hàng</h3>
                <p className="text-slate-500 mt-2">Vui lòng kiểm tra lại quyền truy cập hoặc đăng nhập lại.</p>
            </div>
        );
    }

    return (
        <div className="space-y-8 max-w-5xl">
            <div>
                <h1 className="text-2xl font-bold text-slate-900">Cài đặt hệ thống</h1>
                <p className="text-slate-500">Quản lý hồ sơ nhà hàng và các thiết lập tùy chỉnh</p>
            </div>

            <div className="grid gap-8 lg:grid-cols-4">
                <div className="lg:col-span-1">
                    <nav className="flex flex-col gap-1 sticky top-4">
                        <button className="flex items-center gap-3 px-4 py-2.5 text-sm font-medium rounded-lg bg-slate-900 text-white shadow-sm transition-all">
                            <User className="h-4 w-4" />
                            Thông tin nhà hàng
                        </button>
                        <button className="flex items-center gap-3 px-4 py-2.5 text-sm font-medium rounded-lg text-slate-500 hover:text-slate-900 hover:bg-slate-50 transition-all">
                            <Palette className="h-4 w-4" />
                            Giao diện & Thương hiệu
                        </button>
                        <button className="flex items-center gap-3 px-4 py-2.5 text-sm font-medium rounded-lg text-slate-500 hover:text-slate-900 hover:bg-slate-50 transition-all">
                            <Shield className="h-4 w-4" />
                            Bảo mật & Phân quyền
                        </button>
                    </nav>
                </div>

                <div className="lg:col-span-3 space-y-6">
                    <section className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                        <div className="px-6 py-4 border-b bg-slate-50/50">
                            <h3 className="font-semibold text-slate-900">Hồ sơ nhà hàng</h3>
                            <p className="text-sm text-slate-500">Thông tin cơ bản hiển thị trên Menu và Hóa đơn</p>
                        </div>
                        <div className="p-6 space-y-8">
                            <LogoUpload restaurant={restaurant} />
                            <hr />
                            <RestaurantProfileForm restaurant={restaurant} />
                        </div>
                    </section>

                    <section className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden opacity-60">
                        <div className="px-6 py-4 border-b bg-slate-50/50">
                            <h3 className="font-semibold text-slate-900">Thiết lập Menu (Sắp ra mắt)</h3>
                            <p className="text-sm text-slate-500">Cấu hình cách khách hàng tương tác với Menu số</p>
                        </div>
                        <div className="p-6">
                            <p className="text-sm text-slate-500 italic">Tính năng đang trong quá trình phát triển.</p>
                        </div>
                    </section>
                </div>
            </div>
        </div>
    );
}
