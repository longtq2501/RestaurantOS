"use client";

import { useAuthStore } from "@/store/auth.store";
import { Bell, UserCircle } from "lucide-react";

export function Header() {
    const user = useAuthStore((state) => state.user);

    return (
        <header className="flex h-16 items-center justify-between border-b bg-white px-8">
            <div className="font-medium text-slate-500">
                Welcome back, <span className="text-slate-900">{user?.fullName || "Admin"}</span>
            </div>
            <div className="flex items-center gap-4">
                <button className="relative rounded-full p-2 text-slate-500 hover:bg-slate-100">
                    <Bell className="h-5 w-5" />
                    <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-danger border-2 border-white" />
                </button>
                <div className="flex items-center gap-2 pl-2 border-l">
                    <div className="text-right">
                        <p className="text-xs font-semibold">{user?.fullName}</p>
                        <p className="text-[10px] text-slate-500 uppercase">{user?.role}</p>
                    </div>
                    <UserCircle className="h-8 w-8 text-slate-300" />
                </div>
            </div>
        </header>
    );
}
