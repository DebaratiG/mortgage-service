package com.mortgage.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Monthly cost calculator helper tests")
class MortgageMonthlyCostCalculatorHelperTest {

    private final MortgageMonthlyCostCalculatorHelper calculator = new MortgageMonthlyCostCalculatorHelper();

    @Test
    @DisplayName("Calculate monthly cost standard loan")
    void shouldReturnCorrectAmountWhenCalculateMonthlyCostForStandardLoan() {
        // GIVEN
        BigDecimal loanValue = new BigDecimal("300000");
        BigDecimal interestRate = new BigDecimal("4.5");
        Integer maturityPeriod = 360;

        // WHEN
        BigDecimal result = calculator.calculateMonthlyCost(loanValue, interestRate, maturityPeriod);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result).isGreaterThan(BigDecimal.ZERO);
        assertThat(result.scale()).isEqualTo(2);  // Should have 2 decimal places
    }

    @ParameterizedTest
    @CsvSource({
            "100000, 3.5, 120, 900",     // Approximate expected values
            "200000, 4.0, 240, 1200",
            "300000, 4.5, 360, 1500"
    })
    @DisplayName("Calculate monthly cost with multiple scenarios")
    void shouldReturnValidAmountsWhenCalculateMonthlyCostWithMultipleScenarios(
            String loanValue, String interestRate, Integer maturityPeriod, String minExpected) {
        // GIVEN
        BigDecimal loan = new BigDecimal(loanValue);
        BigDecimal rate = new BigDecimal(interestRate);
        BigDecimal minAmount = new BigDecimal(minExpected);

        // WHEN
        BigDecimal result = calculator.calculateMonthlyCost(loan, rate, maturityPeriod);

        // THEN
        assertThat(result).isGreaterThan(minAmount);
    }

    @Test
    @DisplayName("Calculate monthly cost with high interest rate")
    void shouldReturnHigherPaymentWhenCalculateMonthlyCostWithHighInterestRate() {
        // GIVEN
        BigDecimal loanValue = new BigDecimal("300000");
        BigDecimal lowRate = new BigDecimal("3.0");
        BigDecimal highRate = new BigDecimal("6.0");
        Integer maturityPeriod = 360;

        // WHEN
        BigDecimal lowRatePayment = calculator.calculateMonthlyCost(loanValue, lowRate, maturityPeriod);
        BigDecimal highRatePayment = calculator.calculateMonthlyCost(loanValue, highRate, maturityPeriod);

        // THEN
        assertThat(highRatePayment).isGreaterThan(lowRatePayment);
    }

    @Test
    @DisplayName("Calculate monthly cost for shorter term and increases monthly payment")
    void shouldReturnHigherPaymentWhenCalculateMonthlyCostForShorterTerm() {
        // GIVEN
        BigDecimal loanValue = new BigDecimal("300000");
        BigDecimal interestRate = new BigDecimal("4.5");

        // WHEN
        BigDecimal payment360 = calculator.calculateMonthlyCost(loanValue, interestRate, 360);
        BigDecimal payment180 = calculator.calculateMonthlyCost(loanValue, interestRate, 180);

        // THEN
        assertThat(payment180).isGreaterThan(payment360);
    }
}
