package com.mortgage.exception;

import java.io.Serial;

/**
 * This Exception will be thrown when the mortgage rate is not found.
 */
public class MortgageEligibilityFailedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MortgageEligibilityFailedException(String message) {
        super(message);
    }
}
