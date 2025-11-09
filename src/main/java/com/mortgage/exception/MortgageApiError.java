package com.mortgage.exception;

/**
 * Represents a standardized response structure for errors in the Mortgage APIs.
 * This record is intended to encapsulate error details and provide clarity in error handling responses.
 * <p>
 * Fields:
 *  1. code: A string that uniquely identifies the type of error, allowing clients to interpret the issue programmatically.
 *  2. message: A human-readable message describing the error, providing context for the issue encountered.
 *  3. cause: Cause of the exception described for understanding of the issue.
 *  4. path: The path to the endpoint that caused the error.
 */
public record MortgageApiError(String code, String message, String cause, String path) {
}
