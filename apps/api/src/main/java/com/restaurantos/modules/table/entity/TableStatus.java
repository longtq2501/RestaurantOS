package com.restaurantos.modules.table.entity;

import lombok.Getter;

/**
 * Lifecycle states of a restaurant table.
 */
@Getter
public enum TableStatus {
    EMPTY("Trống"),
    OCCUPIED("Đang có khách"),
    RESERVED("Đã đặt trước"),
    CLEANING("Đang dọn dẹp");

    private final String displayName;

    TableStatus(String displayName) {
        this.displayName = displayName;
    }
}
