"use client";

import { MenuItemForm } from "@/components/menu/MenuItemForm";
import { Button } from "@restaurantos/ui";
import { ChevronLeft } from "lucide-react";
import Link from "next/link";

export default function NewMenuItemPage() {
    return (
        <div className="space-y-6">
            <div className="flex items-center gap-4">
                <Link href="/menu/items">
                    <Button variant="outline" size="sm" className="h-8 w-8 p-0">
                        <ChevronLeft className="h-4 w-4" />
                    </Button>
                </Link>
                <div>
                    <h2 className="text-2xl font-bold tracking-tight">Add New Item</h2>
                    <p className="text-slate-500">Create a new dish for your restaurant menu.</p>
                </div>
            </div>

            <MenuItemForm />
        </div>
    );
}
