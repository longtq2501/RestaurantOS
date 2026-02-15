package com.restaurantos.shared.exception;

/**
 * Exception thrown when authentication fails or is missing.
 * Maps to HTTP 401 Unauthorized.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
