"use client";

import { TableModal } from "@/components/tables/TableModal";
import { tableApi } from "@/lib/api/tables";
import { useAuthStore } from "@/store/auth.store";
import { Table, TableStatus } from "@restaurantos/types";
import {
    Badge,
    Button,
    Card,
    CardContent,
} from "@restaurantos/ui";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Edit, MapPin, Plus, QrCode, RefreshCw, Trash2, Users } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";

const getStatusVariant = (status: TableStatus) => {
    switch (status) {
        case TableStatus.EMPTY: return "success";
        case TableStatus.OCCUPIED: return "destructive";
        case TableStatus.RESERVED: return "warning";
        case TableStatus.CLEANING: return "secondary";
        default: return "outline";
    }
};

const getStatusLabel = (status: TableStatus) => {
    switch (status) {
        case TableStatus.EMPTY: return "Trống";
        case TableStatus.OCCUPIED: return "Có khách";
        case TableStatus.RESERVED: return "Đặt trước";
        case TableStatus.CLEANING: return "Dọn dẹp";
        default: return status;
    }
};

export default function TablesPage() {
    const user = useAuthStore((state) => state.user);
    const queryClient = useQueryClient();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedTable, setSelectedTable] = useState<Table | undefined>(undefined);

    const { data: tablesResponse, isLoading, refetch } = useQuery({
        queryKey: ["tables", user?.restaurantId],
        queryFn: () => tableApi.getTables(user?.restaurantId || ""),
        enabled: !!user?.restaurantId,
    });

    const tables = tablesResponse?.data || [];

    const handleDownloadQrCodes = async () => {
        if (!user?.restaurantId) return;
        try {
            toast.promise(tableApi.downloadQrCodes(user.restaurantId), {
                loading: "Đang tạo mã QR...",
                success: "Đã tải xuống mã QR!",
                error: "Không thể tải mã QR",
            });
        } catch (error) {
            console.error(error);
        }
    };

    const handleDeleteTable = async (id: string) => {
        if (!user?.restaurantId || !confirm("Bạn có chắc chắn muốn xóa bàn này?")) return;

        try {
            await tableApi.deleteTable(user.restaurantId, id);
            toast.success("Đã xóa bàn thành công");
            queryClient.invalidateQueries({ queryKey: ["tables"] });
        } catch (error) {
            toast.error("Không thể xóa bàn");
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Quản lý bàn</h1>
                    <p className="text-slate-500">Quản lý sơ đồ bàn và mã QR cho nhà hàng.</p>
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" size="sm" onClick={handleDownloadQrCodes}>
                        <QrCode className="h-4 w-4 mr-2" />
                        Tải mã QR
                    </Button>
                    <Button size="sm" onClick={() => {
                        setSelectedTable(undefined);
                        setIsModalOpen(true);
                    }}>
                        <Plus className="h-4 w-4 mr-2" />
                        Thêm bàn
                    </Button>
                </div>
            </div>

            <div className="flex items-center justify-between">
                <div className="flex gap-4">
                    <div className="flex items-center gap-2">
                        <div className="h-3 w-3 rounded-full bg-success" />
                        <span className="text-sm text-slate-600">Trống</span>
                    </div>
                    <div className="flex items-center gap-2">
                        <div className="h-3 w-3 rounded-full bg-danger" />
                        <span className="text-sm text-slate-600">Có khách</span>
                    </div>
                    <div className="flex items-center gap-2">
                        <div className="h-3 w-3 rounded-full bg-warning" />
                        <span className="text-sm text-slate-600">Đặt trước</span>
                    </div>
                    <div className="flex items-center gap-2">
                        <div className="h-3 w-3 rounded-full bg-secondary" />
                        <span className="text-sm text-slate-600">Dọn dẹp</span>
                    </div>
                </div>
                <Button variant="outline" size="sm" onClick={() => refetch()} disabled={isLoading}>
                    <RefreshCw className={`h-4 w-4 ${isLoading ? "animate-spin" : ""}`} />
                </Button>
            </div>

            {isLoading ? (
                <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-6">
                    {[...Array(12)].map((_, i) => (
                        <Card key={i} className="animate-pulse bg-slate-100 h-40" />
                    ))}
                </div>
            ) : tables.length === 0 ? (
                <div className="text-center py-24 border-2 border-dashed rounded-xl border-slate-200">
                    <p className="text-slate-500 text-lg">Chưa có bàn nào được tạo.</p>
                    <Button
                        variant="primary"
                        className="mt-4"
                        onClick={() => {
                            setSelectedTable(undefined);
                            setIsModalOpen(true);
                        }}
                    >
                        Thêm bàn đầu tiên
                    </Button>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-6">
                    {tables.map((table) => (
                        <Card
                            key={table.id}
                            className={`group relative overflow-hidden transition-all hover:shadow-md border-t-4 ${table.status === TableStatus.EMPTY ? "border-t-success" :
                                table.status === TableStatus.OCCUPIED ? "border-t-danger" :
                                    table.status === TableStatus.RESERVED ? "border-t-warning" :
                                        "border-t-secondary"
                                }`}
                        >
                            <CardContent className="p-5">
                                <div className="flex justify-between items-start mb-4">
                                    <div className="h-10 w-10 rounded-lg bg-slate-100 flex items-center justify-center font-bold text-lg">
                                        {table.tableNumber}
                                    </div>
                                    <Badge variant={getStatusVariant(table.status)}>
                                        {getStatusLabel(table.status)}
                                    </Badge>
                                </div>
                                <div className="space-y-2">
                                    <div className="flex items-center text-sm text-slate-500">
                                        <Users className="h-3 w-3 mr-2" />
                                        <span>{table.capacity} chỗ</span>
                                    </div>
                                    <div className="flex items-center text-sm text-slate-500">
                                        <MapPin className="h-3 w-3 mr-2" />
                                        <span>{table.section || "Sảnh chính"}</span>
                                    </div>
                                </div>

                                <div className="absolute top-2 right-2 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <button
                                        onClick={() => { setSelectedTable(table); setIsModalOpen(true); }}
                                        className="p-1 hover:bg-slate-100 rounded text-slate-600"
                                    >
                                        <Edit className="h-4 w-4" />
                                    </button>
                                    <button
                                        onClick={() => handleDeleteTable(table.id)}
                                        className="p-1 hover:bg-danger/10 rounded text-danger"
                                    >
                                        <Trash2 className="h-4 w-4" />
                                    </button>
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            )}

            <TableModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                table={selectedTable}
            />
        </div>
    );
}
