"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { MenuItem } from "@restaurantos/types";
import {
    Button,
    Input,
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue
} from "@restaurantos/ui";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import * as z from "zod";

const menuItemSchema = z.object({
    name: z.string().min(2, "Name must be at least 2 characters"),
    description: z.string().optional(),
    price: z.coerce.number().min(0, "Price must be positive"),
    categoryId: z.string().min(1, "Please select a category"),
    isAvailable: z.boolean().default(true),
    isFeatured: z.boolean().default(false),
    displayOrder: z.coerce.number().default(0),
    prepTime: z.coerce.number().optional(),
    spicyLevel: z.coerce.number().min(0).max(5).default(0),
    isVegetarian: z.boolean().default(false),
    allergens: z.string().optional(),
});

type MenuItemFormValues = z.infer<typeof menuItemSchema>;

interface MenuItemFormProps {
    initialData?: MenuItem;
}

export function MenuItemForm({ initialData }: MenuItemFormProps) {
    const router = useRouter();
    const {
        register,
        handleSubmit,
        setValue,
        watch,
        formState: { errors, isSubmitting },
    } = useForm<MenuItemFormValues>({
        resolver: zodResolver(menuItemSchema),
        defaultValues: initialData ? {
            name: initialData.name,
            description: initialData.description || "",
            price: initialData.price,
            categoryId: initialData.categoryId,
            isAvailable: initialData.isAvailable,
            isFeatured: initialData.isFeatured,
            displayOrder: initialData.displayOrder,
            prepTime: initialData.prepTime,
            spicyLevel: initialData.spicyLevel,
            isVegetarian: initialData.isVegetarian,
            allergens: initialData.allergens,
        } : {
            isAvailable: true,
            displayOrder: 0,
            spicyLevel: 0,
        },
    });

    const categoryId = watch("categoryId");

    const onSubmit = async (data: MenuItemFormValues) => {
        try {
            console.log("Submitting:", data);
            // Simulate API call
            await new Promise(resolve => setTimeout(resolve, 1000));

            toast.success(initialData ? "Item updated successfully" : "Item created successfully");
            router.push("/menu/items");
            router.refresh();
        } catch (error) {
            toast.error("Something went wrong");
        }
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-8 bg-white p-6 rounded-lg border">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-6">
                    <div className="space-y-2">
                        <label className="text-sm font-medium">Item Name</label>
                        <Input {...register("name")} placeholder="e.g. Pepperoni Pizza" />
                        {errors.name && <p className="text-xs text-danger">{errors.name.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-medium">Description</label>
                        <textarea
                            {...register("description")}
                            className="flex min-h-[100px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-slate-500 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                            placeholder="Tell us about this dish..."
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Price (₫)</label>
                            <Input {...register("price")} type="number" placeholder="100000" />
                            {errors.price && <p className="text-xs text-danger">{errors.price.message}</p>}
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-medium">Category</label>
                            <Select
                                value={categoryId}
                                onValueChange={(val) => setValue("categoryId", val, { shouldValidate: true })}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder="Select Category" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="1">Main Course</SelectItem>
                                    <SelectItem value="2">Appetizers</SelectItem>
                                    <SelectItem value="3">Desserts</SelectItem>
                                </SelectContent>
                            </Select>
                            {errors.categoryId && <p className="text-xs text-danger">{errors.categoryId.message}</p>}
                        </div>
                    </div>
                </div>

                <div className="space-y-6">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Prep Time (mins)</label>
                            <Input {...register("prepTime")} type="number" placeholder="15" />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Spicy Level (0-5)</label>
                            <Input {...register("spicyLevel")} type="number" min={0} max={5} />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-medium">Allergens</label>
                        <Input {...register("allergens")} placeholder="e.g. Peanut, Milk" />
                    </div>

                    <div className="flex flex-wrap gap-6">
                        <div className="flex items-center gap-2">
                            <input type="checkbox" {...register("isAvailable")} id="isAvailable" className="h-4 w-4 rounded border-slate-300 text-primary focus:ring-primary" />
                            <label htmlFor="isAvailable" className="text-sm font-medium">Available for Order</label>
                        </div>
                        <div className="flex items-center gap-2">
                            <input type="checkbox" {...register("isFeatured")} id="isFeatured" className="h-4 w-4 rounded border-slate-300 text-primary focus:ring-primary" />
                            <label htmlFor="isFeatured" className="text-sm font-medium">Feature on Homepage</label>
                        </div>
                        <div className="flex items-center gap-2">
                            <input type="checkbox" {...register("isVegetarian")} id="isVegetarian" className="h-4 w-4 rounded border-slate-300 text-primary focus:ring-primary" />
                            <label htmlFor="isVegetarian" className="text-sm font-medium">Vegetarian</label>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-medium">Display Order</label>
                        <Input {...register("displayOrder")} type="number" />
                    </div>
                </div>
            </div>

            <div className="flex justify-end gap-4 border-t pt-6">
                <Button variant="outline" type="button" onClick={() => router.back()}>Cancel</Button>
                <Button type="submit" disabled={isSubmitting}>
                    {isSubmitting ? "Saving..." : initialData ? "Update Item" : "Create Item"}
                </Button>
            </div>
        </form>
    );
}
