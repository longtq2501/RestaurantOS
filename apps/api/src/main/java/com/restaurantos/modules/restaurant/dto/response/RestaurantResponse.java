package com.restaurantos.modules.restaurant.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.restaurantos.modules.restaurant.entity.SubscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload representing restaurant information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    private UUID id;
    private String name;
    private String slug;
    private String address;
    private String phone;
    private String email;
    private String logoUrl;
    private String themeColor;
    private String customDomain;
    private SubscriptionPlan plan;
    private String planDisplayName;
    private LocalDateTime subscriptionExpiresAt;
    private String settings;
}
