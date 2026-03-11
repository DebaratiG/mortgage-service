package com.mortgage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a mortgage interest rate tied to a specific loan maturity period.
 * <p>
 * This record encapsulates the interest rate applicable for a given maturity period.
 *
 */
@Schema(name = "MortgageRateDTO", description = "Represents a mortgage interest rate payload")
public record MortgageRateDTO(

        @Schema(name="maturityPeriod", description = "The loan term in months for which the interest rate applies")
        @NotNull @Positive
        Integer maturityPeriod,

        @Schema(name="interestRate", description = "The annual interest rate percentage as a decimal value")
        @NotNull @Positive
        BigDecimal interestRate,

        @Schema(name="lastUpdated", description = "The timestamp of the last time the rate was updated")
        @NotNull @PastOrPresent(message = "lastUpdated must be in the past or present")
        Instant lastUpdated,

        @Schema(name="type", description = "The type of the rate record")
        @NotBlank
        String type) {}
