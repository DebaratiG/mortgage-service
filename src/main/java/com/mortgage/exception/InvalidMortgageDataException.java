package com.mortgage.exception;

import java.io.Serial;

public class InvalidMortgageDataException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidMortgageDataException(String message) {
        super(message);
    };

    public InvalidMortgageDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
