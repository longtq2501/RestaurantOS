"use client";

import apiClient from "@/lib/api/client";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button, Card, CardContent, CardHeader, CardTitle, Input } from "@restaurantos/ui";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import * as z from "zod";

const registerSchema = z.object({
    username: z.string().min(3, "Username must be at least 3 characters"),
    email: z.string().email("Invalid email address"),
    password: z.string().min(6, "Password must be at least 6 characters"),
    fullName: z.string().min(1, "Full name is required"),
    restaurantName: z.string().min(1, "Restaurant name is required"),
});

type RegisterFormValues = z.infer<typeof registerSchema>;

export default function RegisterPage() {
    const router = useRouter();
    const [isLoading, setIsLoading] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<RegisterFormValues>({
        resolver: zodResolver(registerSchema),
    });

    const onSubmit = async (data: RegisterFormValues) => {
        setIsLoading(true);
        try {
            await apiClient.post("/auth/register", data);
            toast.success("Registration successful! You can now login.");
            router.push("/login");
        } catch (error: any) {
            toast.error(error.response?.data?.message || "Registration failed. Please try again.");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center bg-slate-50 py-12">
            <Card className="w-full max-w-md">
                <CardHeader>
                    <CardTitle className="text-center text-2xl">Create Account</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Full Name</label>
                            <Input
                                {...register("fullName")}
                                placeholder="Enter your full name"
                                error={errors.fullName?.message}
                            />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Email</label>
                            <Input
                                {...register("email")}
                                type="email"
                                placeholder="Enter your email"
                                error={errors.email?.message}
                            />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Username</label>
                            <Input
                                {...register("username")}
                                placeholder="Choose a username"
                                error={errors.username?.message}
                            />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Restaurant Name</label>
                            <Input
                                {...register("restaurantName")}
                                placeholder="Name of your restaurant"
                                error={errors.restaurantName?.message}
                            />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Password</label>
                            <Input
                                {...register("password")}
                                type="password"
                                placeholder="Create a password"
                                error={errors.password?.message}
                            />
                        </div>
                        <Button
                            type="submit"
                            className="w-full"
                            disabled={isLoading}
                        >
                            {isLoading ? "Creating account..." : "Register"}
                        </Button>
                        <div className="text-center text-sm">
                            Already have an account?{" "}
                            <Button
                                variant="ghost"
                                className="h-auto p-0 text-primary hover:bg-transparent"
                                onClick={() => router.push("/login")}
                            >
                                Login
                            </Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}
