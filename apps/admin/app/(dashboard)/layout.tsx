"use client";

import { Header } from "@/components/layout/Header";
import { Sidebar } from "@/components/layout/Sidebar";
import { useOrderWebSocket } from "@/hooks/useOrderWebSocket";

export default function DashboardLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    // Initialize WebSocket connection for the entire dashboard
    useOrderWebSocket();

    return (
        <div className="flex h-screen bg-slate-50 overflow-hidden">
            <Sidebar />
            <div className="flex flex-1 flex-col">
                <Header />
                <main className="flex-1 overflow-y-auto p-8">
                    {children}
                </main>
            </div>
        </div>
    );
}
