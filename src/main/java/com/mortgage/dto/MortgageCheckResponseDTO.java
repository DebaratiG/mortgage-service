package com.mortgage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 *
 * Represents the response for a mortgage eligibility and affordability check.
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
public class MortgageCheckResponseDTO {

    /**
     * Returns true if the applicant is eligible and false if not eligible for the requested mortgage.
     */
    public boolean eligible;

    /**
     * Represents the calculated monthly payment for the requested mortgage.
     */
    public BigDecimal monthlyCosts;

    /**
     * Describes the reasoning behind the eligibility rejection or affordability assessment.
     */
    public String reason;
}
