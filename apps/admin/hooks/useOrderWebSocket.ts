"use client";

import { useAuthStore } from "@/store/auth.store";
import { Client } from "@stomp/stompjs";
import { useQueryClient } from "@tanstack/react-query";
import { useCallback, useEffect, useRef } from "react";
import SockJS from "sockjs-client";
import { toast } from "sonner";

export function useOrderWebSocket() {
    const user = useAuthStore((state) => state.user);
    const queryClient = useQueryClient();
    const stompClientRef = useRef<Client | null>(null);

    const playNotificationSound = useCallback(() => {
        const audio = new Audio("/sounds/new-order.mp3");
        audio.play().catch(err => console.error("Error playing sound:", err));
    }, []);

    useEffect(() => {
        if (!user?.restaurantId) return;

        const socket = new SockJS(`${process.env.NEXT_PUBLIC_API_URL}/ws`);
        const client = new Client({
            webSocketFactory: () => socket,
            debug: (str) => {
                // console.log(str);
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = (frame) => {
            // console.log("Connected to WebSocket");
            
            // Subscribe to restaurant dashboard updates
            client.subscribe(`/topic/restaurants/${user.restaurantId}/dashboard`, (message) => {
                const data = JSON.parse(message.body);
                // console.log("Received message from dashboard:", data);
                
                // Invalidate orders query to refresh the list
                queryClient.invalidateQueries({ queryKey: ["orders"] });
                
                // If it's a new order or important update, notify user
                if (data.type === "NEW_ORDER") {
                    toast.info(`New Order: ${data.orderNumber}`, {
                        description: `Table ${data.tableNumber} - ${data.totalAmount} VND`,
                    });
                    playNotificationSound();
                } else if (data.type === "ORDER_UPDATE") {
                    toast.success(`Order ${data.orderNumber} updated to ${data.status}`);
                    // Also invalidate specific order if open
                    queryClient.invalidateQueries({ queryKey: ["order", data.orderId] });
                }
            });
        };

        client.onStompError = (frame) => {
            console.error("Broker reported error: " + frame.headers["message"]);
            console.error("Additional details: " + frame.body);
        };

        client.activate();
        stompClientRef.current = client;

        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.deactivate();
            }
        };
    }, [user?.restaurantId, queryClient, playNotificationSound]);

    return {
        isConnected: stompClientRef.current?.connected || false,
    };
}
