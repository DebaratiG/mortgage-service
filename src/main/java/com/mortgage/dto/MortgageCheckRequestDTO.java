package com.mortgage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Represents a request for a mortgage eligibility and affordability check.
 * <p>
 * This record encapsulates essential financial details necessary to assess
 * the mortgage request, including the applicant's income, the desired loan
 * value, the maturity period for the mortgage, and the value of the home
 * being purchased or refinanced.
 * <p>
 * The fields in this request must adhere to specific validation constraints:
 *  1. All fields must be non-null.
 *  2. All values must be positive numbers.
 * <p>
 * The constraints ensure that invalid or incomplete data is not processed.
 * <p>
 */
@Schema(name = "MortgageCheckRequestDTO", description = "Represents a mortgage check request payload")
public record MortgageCheckRequestDTO(
            @Schema(name="income", description = "The applicant's annual income")
            @NotNull @Positive BigDecimal income,

            @Schema(name="maturityPeriod", description = "The loan repayment period, expressed in months")
            @NotNull @Positive Integer maturityPeriod,

            @Schema(name="loanValue", description = "The requested loan amount")
            @NotNull @Positive BigDecimal loanValue,

            @Schema(name="homeValue", description = "The value of the property associated with the loan")
            @NotNull @Positive BigDecimal homeValue) {}
