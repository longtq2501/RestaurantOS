"use client";

import { useAuthStore } from "@/store/auth.store";
import {
    BarChart3,
    ClipboardList,
    LayoutDashboard,
    LogOut,
    Package,
    Settings,
    Table,
    UtensilsCrossed
} from "lucide-react";
import Link from "next/link";
import { usePathname } from "next/navigation";

// Local cn helper if import fails
const clsn = (...inputs: any[]) => inputs.filter(Boolean).join(" ");

const menuItems = [
    { icon: LayoutDashboard, label: "Dashboard", href: "/" },
    { icon: ClipboardList, label: "Orders", href: "/orders" },
    { icon: UtensilsCrossed, label: "Menu", href: "/menu" },
    { icon: Table, label: "Tables", href: "/tables" },
    { icon: Package, label: "Inventory", href: "/inventory" },
    { icon: BarChart3, label: "Analytics", href: "/analytics" },
    { icon: Settings, label: "Settings", href: "/settings" },
];

export function Sidebar() {
    const pathname = usePathname();
    const logout = useAuthStore((state) => state.logout);

    return (
        <div className="flex h-full w-64 flex-col border-r bg-white">
            <div className="flex h-16 items-center border-b px-6">
                <span className="text-xl font-bold text-primary">RestaurantOS</span>
            </div>
            <nav className="flex-1 space-y-1 p-4">
                {menuItems.map((item) => {
                    const isActive = pathname === item.href;
                    return (
                        <Link
                            key={item.href}
                            href={item.href}
                            className={clsn(
                                "flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                                isActive
                                    ? "bg-primary text-white"
                                    : "text-slate-600 hover:bg-slate-100 hover:text-slate-900"
                            )}
                        >
                            <item.icon className="h-5 w-5" />
                            {item.label}
                        </Link>
                    );
                })}
            </nav>
            <div className="border-t p-4">
                <button
                    onClick={logout}
                    className="flex w-full items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-danger hover:bg-danger/10 transition-colors"
                >
                    <LogOut className="h-5 w-5" />
                    Logout
                </button>
            </div>
        </div>
    );
}
