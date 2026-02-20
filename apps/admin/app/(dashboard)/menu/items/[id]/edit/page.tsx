"use client";

import { MenuItemForm } from "@/components/menu/MenuItemForm";
import { MenuItem } from "@restaurantos/types";
import { Button } from "@restaurantos/ui";
import { ChevronLeft, Loader2 } from "lucide-react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";

export default function EditMenuItemPage() {
    const params = useParams();
    const id = params.id as string;
    const [item, setItem] = useState<MenuItem | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Simulate fetching item
        const timer = setTimeout(() => {
            setItem({
                id: id,
                name: "Classic Cheeseburger",
                description: "Juicy beef patty with cheddar cheese and fresh lettuce.",
                price: 125000,
                categoryId: "1",
                isAvailable: true,
                displayOrder: 1,
                isFeatured: true,
                orderCount: 45,
                ratingAvg: 4.8,
                ratingCount: 12,
                createdAt: new Date().toISOString(),
                updatedAt: new Date().toISOString(),
            });
            setLoading(false);
        }, 500);

        return () => clearTimeout(timer);
    }, [id]);

    if (loading) {
        return (
            <div className="flex h-[400px] items-center justify-center">
                <Loader2 className="h-8 w-8 animate-spin text-primary" />
            </div>
        );
    }

    if (!item) {
        return <div>Item not found</div>;
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center gap-4">
                <Link href="/menu/items">
                    <Button variant="outline" size="sm" className="h-8 w-8 p-0">
                        <ChevronLeft className="h-4 w-4" />
                    </Button>
                </Link>
                <div>
                    <h2 className="text-2xl font-bold tracking-tight">Edit Menu Item</h2>
                    <p className="text-slate-500">Update item details, pricing, and availability.</p>
                </div>
            </div>

            <MenuItemForm initialData={item} />
        </div>
    );
}
