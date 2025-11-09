package com.mortgage.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test-uri");
    }

    @Test
    void testHandleValidationExceptions() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "income", "must be positive");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<MortgageApiError> response = handler.handleValidationExceptions(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertNotNull(response.getBody());
        assertThat(response.getBody().message()).contains("income: must be positive");
        assertThat(response.getBody().path()).isEqualTo("/test-uri");
    }

    @Test
    void testHandleHttpMessageNotReadable() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON",
                new RuntimeException("parse error"));

        ResponseEntity<MortgageApiError> response = handler.handleHttpMessageNotReadable(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertNotNull(response.getBody());
        assertThat(response.getBody().message())
                .contains("Invalid request format");
    }

    @Test
    void testHandleMortgageEligibilityFailedException() {
        MortgageEligibilityFailedException ex =
                new MortgageEligibilityFailedException("Loan exceeds 4x income");

        ResponseEntity<MortgageApiError> response = handler.handleTypeMismatch(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertNotNull(response.getBody());
        assertThat(response.getBody().message()).isEqualTo("Loan exceeds 4x income");
    }

    @Test
    void testHandleGeneralException() {
        Exception ex = new Exception("Unexpected failure");

        ResponseEntity<MortgageApiError> response = handler.handleGeneralException(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertNotNull(response.getBody());
        assertThat(response.getBody().message()).contains("Unexpected failure");
    }


    @Test
    void testHandleConstraintViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(PathImpl.createPathFromString("interestRate"));

        when(violation.getMessage()).thenReturn("must be positive");

        ConstraintViolationException ex =
                new ConstraintViolationException(Set.of(violation));

        ResponseEntity<MortgageApiError> response = handler.handleConstraintViolation(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertNotNull(response.getBody());
        assertThat(response.getBody().message())
                .contains("interestRate: must be positive");
    }
}