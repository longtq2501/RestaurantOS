"use client";

import { restaurantApi } from "@/lib/api/restaurant";
import { Restaurant } from "@restaurantos/types";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Camera, Loader2, X } from "lucide-react";
import { useRef, useState } from "react";
import { toast } from "sonner";

interface LogoUploadProps {
    restaurant: Restaurant;
}

export function LogoUpload({ restaurant }: LogoUploadProps) {
    const queryClient = useQueryClient();
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [preview, setPreview] = useState<string | null>(restaurant.logoUrl || null);

    const mutation = useMutation({
        mutationFn: (file: File) => restaurantApi.uploadLogo(restaurant.id, file),
        onSuccess: (res) => {
            toast.success("Cập nhật logo thành công");
            queryClient.invalidateQueries({ queryKey: ["restaurant-profile"] });
            setPreview(res.data.logoUrl || null);
        },
        onError: (error: any) => {
            toast.error(error.response?.data?.message || "Có lỗi xảy ra khi tải ảnh lên");
        },
    });

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            if (file.size > 2 * 1024 * 1024) {
                toast.error("Vui lòng chọn ảnh nhỏ hơn 2MB");
                return;
            }
            mutation.mutate(file);
        }
    };

    const handleClick = () => {
        fileInputRef.current?.click();
    };

    return (
        <div className="flex flex-col items-center gap-4 sm:flex-row sm:items-start">
            <div className="relative group">
                <div
                    className="h-32 w-32 rounded-xl border-2 border-dashed border-slate-200 bg-slate-50 flex items-center justify-center overflow-hidden cursor-pointer hover:border-primary transition-all"
                    onClick={handleClick}
                >
                    {mutation.isPending ? (
                        <Loader2 className="h-8 w-8 text-primary animate-spin" />
                    ) : preview ? (
                        <img
                            src={preview}
                            alt="Restaurant Logo"
                            className="h-full w-full object-cover"
                        />
                    ) : (
                        <div className="text-center p-4">
                            <Camera className="h-8 w-8 text-slate-400 mx-auto mb-2" />
                            <span className="text-xs text-slate-500 font-medium">Tải Logo</span>
                        </div>
                    )}
                </div>

                {preview && !mutation.isPending && (
                    <button
                        className="absolute -top-2 -right-2 h-6 w-6 rounded-full bg-slate-900 text-white flex items-center justify-center shadow-lg hover:bg-slate-700 transition-colors"
                        onClick={(e) => {
                            e.stopPropagation();
                            setPreview(null);
                        }}
                    >
                        <X className="h-4 w-4" />
                    </button>
                )}
            </div>

            <div className="flex-1 space-y-1">
                <h4 className="font-semibold text-slate-900">Logo nhà hàng</h4>
                <p className="text-sm text-slate-500 leading-relaxed">
                    Định dạng hỗ trợ: JPG, PNG, WEBP. <br />
                    Dung lượng tối đa 2MB. Kích thước khuyến nghị: 512x512px.
                </p>
                <input
                    type="file"
                    ref={fileInputRef}
                    className="hidden"
                    accept="image/*"
                    onChange={handleFileChange}
                />
                <button
                    className="text-sm font-medium text-primary hover:underline mt-2"
                    onClick={handleClick}
                    disabled={mutation.isPending}
                >
                    Thay đổi ảnh
                </button>
            </div>
        </div>
    );
}
