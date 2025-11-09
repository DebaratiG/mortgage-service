package com.mortgage.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

/**
 * Global exception handler for the mortgage service application.
 * Handles various exceptions and returns standardized error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from @Valid annotation on request body.
     * Returns 422 Unprocessable Entity with field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MortgageApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField()+": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn(message);
        return toResponse(HttpStatus.UNPROCESSABLE_ENTITY, message, request);
    }

    /**
     * Handles malformed JSON or invalid request body format.
     * Returns 400 Bad Requests.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MortgageApiError> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        String message = "Invalid request format. Please check your JSON syntax and data types.";
        log.warn(message);
        return toResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    /**
     * If any of the eligibility check fails(eg loan value > 4x of income).
     * Returns 400 Bad Request as request data is not valid
     */
    @ExceptionHandler(MortgageEligibilityFailedException.class)
    public ResponseEntity<MortgageApiError> handleTypeMismatch(
            MortgageEligibilityFailedException ex, HttpServletRequest request) {

        log.warn("Bad Request: {}", ex.getMessage());
        return toResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Handles all other unexpected exceptions.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MortgageApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.warn("Unexpected error: {}", ex.getMessage());
        return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, String.join("An Unexpected error occurred. ", ex.getMessage()), request);
    }

    /**
     * Returns ConstraintViolationException with an error message if a data constraint violation is encountered.
     * This exception only matters when the service capability is exposed to insert interest rates.
     * @param ex ConstraintViolationException
     * @return ResponseEntity<String> with an error message
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MortgageApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        log.warn(message);
        return toResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    private ResponseEntity<MortgageApiError> toResponse(HttpStatus status, String message, HttpServletRequest request) {
        MortgageApiError body = new MortgageApiError(
                status.value(),
                message,
                status.getReasonPhrase(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
