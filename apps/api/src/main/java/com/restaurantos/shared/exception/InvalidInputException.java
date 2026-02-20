package com.restaurantos.shared.exception;

/**
 * Exception thrown when input validation or business rules fail.
 * Maps to HTTP 400 Bad Request.
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
