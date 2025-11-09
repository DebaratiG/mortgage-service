package com.mortgage.exception;

import java.io.Serial;

/**
 * This Exception will be thrown when the mortgage rate is not found.
 */
public class MortgageRateNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MortgageRateNotFoundException(String message) {
        super(message);
    }

    public MortgageRateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
