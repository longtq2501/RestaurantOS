"use client";

import { MenuItem } from "@restaurantos/types";
import {
    Badge,
    Button,
    Input,
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow
} from "@restaurantos/ui";
import {
    Edit,
    Filter,
    Image as ImageIcon,
    Plus,
    Search,
    Trash2
} from "lucide-react";
import Link from "next/link";
import { useState } from "react";

const MOCK_ITEMS: MenuItem[] = [
    {
        id: "1",
        name: "Classic Cheeseburger",
        description: "Juicy beef patty with cheddar cheese and fresh lettuce.",
        price: 125000,
        categoryId: "1",
        categoryName: "Main Course",
        isAvailable: true,
        displayOrder: 1,
        isFeatured: true,
        orderCount: 45,
        ratingAvg: 4.8,
        ratingCount: 12,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
    },
    {
        id: "2",
        name: "Margarita Pizza",
        description: "Classic pizza with tomato sauce, mozzarella, and fresh basil.",
        price: 185000,
        categoryId: "1",
        categoryName: "Main Course",
        isAvailable: true,
        displayOrder: 2,
        isFeatured: false,
        orderCount: 32,
        ratingAvg: 4.5,
        ratingCount: 8,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
    },
    {
        id: "3",
        name: "Spring Rolls",
        description: "Crispy rolls with fresh vegetables and sweet chili sauce.",
        price: 65000,
        categoryId: "2",
        categoryName: "Appetizers",
        isAvailable: false,
        displayOrder: 1,
        isFeatured: false,
        orderCount: 15,
        ratingAvg: 4.2,
        ratingCount: 5,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
    }
];

export default function MenuItemsPage() {
    const [items, setItems] = useState<MenuItem[]>(MOCK_ITEMS);
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("all");

    const filteredItems = items.filter(item => {
        const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesCategory = selectedCategory === "all" || item.categoryId === selectedCategory;
        return matchesSearch && matchesCategory;
    });

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h2 className="text-2xl font-bold tracking-tight">Menu Items</h2>
                    <p className="text-slate-500">Add and manage the dishes in your restaurant menu.</p>
                </div>

                <Link href="/menu/items/new">
                    <Button className="flex items-center gap-2">
                        <Plus className="h-4 w-4" />
                        Add New Item
                    </Button>
                </Link>
            </div>

            <div className="flex flex-col md:flex-row items-center gap-4">
                <div className="relative w-full max-w-sm">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
                    <Input
                        placeholder="Search items..."
                        className="pl-9"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>

                <div className="w-full md:w-48">
                    <Select value={selectedCategory} onValueChange={setSelectedCategory}>
                        <SelectTrigger>
                            <div className="flex items-center gap-2">
                                <Filter className="h-4 w-4 text-slate-400" />
                                <SelectValue placeholder="All Categories" />
                            </div>
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">All Categories</SelectItem>
                            <SelectItem value="1">Main Course</SelectItem>
                            <SelectItem value="2">Appetizers</SelectItem>
                        </SelectContent>
                    </Select>
                </div>
            </div>

            <div className="rounded-md border bg-white overflow-hidden">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead className="w-[50px]"></TableHead>
                            <TableHead>Name</TableHead>
                            <TableHead>Category</TableHead>
                            <TableHead>Price</TableHead>
                            <TableHead>Status</TableHead>
                            <TableHead className="text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {filteredItems.map((item) => (
                            <TableRow key={item.id}>
                                <TableCell>
                                    <div className="h-10 w-10 rounded bg-slate-100 flex items-center justify-center text-slate-400 overflow-hidden">
                                        {item.imageUrl ? (
                                            <img src={item.imageUrl} alt={item.name} className="h-full w-full object-cover" />
                                        ) : (
                                            <ImageIcon className="h-5 w-5" />
                                        )}
                                    </div>
                                </TableCell>
                                <TableCell>
                                    <div className="font-bold">{item.name}</div>
                                    <div className="text-xs text-slate-500 truncate max-w-[250px]">{item.description}</div>
                                </TableCell>
                                <TableCell>
                                    <Badge variant="outline">{item.categoryName}</Badge>
                                </TableCell>
                                <TableCell className="font-medium">₫{item.price.toLocaleString()}</TableCell>
                                <TableCell>
                                    <Badge variant={item.isAvailable ? "success" : "secondary"}>
                                        {item.isAvailable ? "Available" : "Sold Out"}
                                    </Badge>
                                </TableCell>
                                <TableCell className="text-right">
                                    <div className="flex justify-end gap-2">
                                        <Button variant="ghost" size="sm" asChild>
                                            <Link href={`/menu/items/${item.id}/edit`}>
                                                <Edit className="h-4 w-4" />
                                            </Link>
                                        </Button>
                                        <Button variant="ghost" size="sm" className="text-danger hover:text-danger hover:bg-danger/10">
                                            <Trash2 className="h-4 w-4" />
                                        </Button>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ))}
                        {filteredItems.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={6} className="h-24 text-center text-slate-500">
                                    No items found.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
}
