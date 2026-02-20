package com.restaurantos.modules.auth.entity;

import lombok.Getter;

/**
 * Roles for system users.
 */
@Getter
public enum UserRole {
    OWNER("Chủ nhà hàng"),
    MANAGER("Quản lý"),
    STAFF("Nhân viên"),
    KITCHEN("Bếp");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public boolean isManagement() {
        return this == OWNER || this == MANAGER;
    }
}
