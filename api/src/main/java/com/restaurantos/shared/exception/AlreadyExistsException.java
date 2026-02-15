package com.restaurantos.shared.exception;

/**
 * Exception thrown when a resource already exists (e.g., duplicate username).
 * Maps to HTTP 409 Conflict.
 */
public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
