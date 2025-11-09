package com.mortgage.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Helper class for calculating the monthly cost of a mortgage using the amortization formula.
 */
@Component
public class MortgageMonthlyCostCalculatorHelper {

    public static final int MONTHLY_COST_MULTIPLIER = 12;

    /**
     *
     * The calculation considers the loan value, annual interest rate, and the
     * maturity period expressed in months. The formula assumes fixed monthly payments.
     * <p>
     * Formula: M = P * [r(1+r)^n] / [(1+r)^n - 1]
     * Where:
     *  - M = Monthly payment
     *  - P = Principal loan amount
     *  - r = Monthly interest rate (annual rate / 12 / 100)
     *  - n = Number of payments (maturity period in months)
     *
     * @param loanValue the principal amount of the loan; must be a positive value
     * @param interestRate the annual interest rate as a percentage (e.g., 5 for 5%); must be a positive value
     * @param maturityPeriodInMonths the loan repayment period expressed in months; must be a positive integer
     *
     * @return the calculated monthly payment amount as a BigDecimal rounded to two decimal places
     *
     */
    public BigDecimal calculateMonthlyCost(BigDecimal loanValue, BigDecimal interestRate, Integer maturityPeriodInMonths) {

        // find the monthly rate from the annual rate
        var monthlyRate = interestRate.divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        // Calculate using the amortization formula
        var onePlusRate = BigDecimal.ONE.add(monthlyRate);
        var onePlusRatePowerN = onePlusRate.pow(maturityPeriodInMonths);

        var numerator = loanValue.multiply(monthlyRate).multiply(onePlusRatePowerN);
        var denominator = onePlusRatePowerN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);

    }

}
