package com.restaurantos.shared.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionTestController {
    @GetMapping("/test/not-found")
    public void throwNotFound() {
        throw new ResourceNotFoundException("Not found message");
    }

    @GetMapping("/test/conflict")
    public void throwConflict() {
        throw new AlreadyExistsException("Conflict message");
    }

    @GetMapping("/test/invalid")
    public void throwInvalid() {
        throw new InvalidInputException("Invalid message");
    }

    @GetMapping("/test/unauthorized")
    public void throwUnauthorized() {
        throw new UnauthorizedException("Unauthorized message");
    }

    @GetMapping("/test/error")
    public void throwError() {
        throw new RuntimeException("Unexpected error");
    }
}
