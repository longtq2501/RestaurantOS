"use client";

import { Card, CardContent } from "@restaurantos/ui";
import { LucideIcon } from "lucide-react";

interface StatsCardProps {
    title: string;
    value: string | number;
    icon: LucideIcon;
    trend?: {
        value: number;
        isPositive: boolean;
    };
    description?: string;
}

export function StatsCard({
    title,
    value,
    icon: Icon,
    trend,
    description
}: StatsCardProps) {
    return (
        <Card>
            <CardContent className="p-6">
                <div className="flex items-center justify-between">
                    <p className="text-sm font-medium text-slate-500">{title}</p>
                    <Icon className="h-4 w-4 text-slate-400" />
                </div>
                <div className="mt-2 flex items-baseline gap-2">
                    <h3 className="text-2xl font-bold text-slate-900">{value}</h3>
                    {trend && (
                        <span className={`text-xs font-medium ${trend.isPositive ? "text-success" : "text-danger"}`}>
                            {trend.isPositive ? "+" : "-"}{Math.abs(trend.value)}%
                        </span>
                    )}
                </div>
                {description && (
                    <p className="mt-1 text-xs text-slate-500">{description}</p>
                )}
            </CardContent>
        </Card>
    );
}
