package com.mortgage.exception;

import java.io.Serial;

/**
 * This Exception will be thrown when the request is invalid.
 */
public class BadRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
