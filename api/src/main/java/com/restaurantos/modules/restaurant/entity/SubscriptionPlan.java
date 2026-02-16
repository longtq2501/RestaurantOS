package com.restaurantos.modules.restaurant.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionPlan {
    FREE("Free"),
    STARTER("Starter"),
    PRO("Professional"),
    ENTERPRISE("Enterprise");

    private final String displayName;
}
