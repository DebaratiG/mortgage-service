package com.mortgage.rule;

import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Home value eligibility strategy tests")
class MortgageEligibilityCheckByHomeValueTest {

    private final MortgageEligibilityCheckByHomeValue homeValueCheck = new MortgageEligibilityCheckByHomeValue();

    @Test
    @DisplayName("Check eligibility with loan less than home value")
    void shouldReturnSuccessWhenCheckEligibilityWithLoanLessThanHomeValue() {
        // GIVEN
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),
                360,
                new BigDecimal("300000"),  // Loan
                new BigDecimal("350000")   // Home value (greater than loan)
        );

        // WHEN
        MortgageCheckResponseDTO result = homeValueCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isTrue();
    }

    @Test
    @DisplayName("Check Eligibility with loan exceeding home value")
    void shouldReturnInEligibleWhenCheckEligibilityWithLoanExceedingHomeValue() {
        // GIVEN
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),
                360,
                new BigDecimal("400000"),  // Loan
                new BigDecimal("350000")   // Home value (less than loan)
        );

        // WHEN
        MortgageCheckResponseDTO result = homeValueCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("less than loan value");
    }

    @Test
    @DisplayName("Check eligibility with loan equals home value")
    void shouldReturnEligibleWhenCheckEligibilityWithLoanEqualsHomeValue() {
        // GIVEN
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),
                360,
                new BigDecimal("350000"),  // Loan
                new BigDecimal("350000")   // Home value (equal to loan)
        );

        // WHEN
        MortgageCheckResponseDTO result = homeValueCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isTrue();
    }

    @Test
    @DisplayName("Check eligibility with null home value")
    void shouldReturnIneligibleWhenCheckEligibilityWithNullHomeValue() {
        // GIVEN
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),
                360,
                new BigDecimal("300000"),   // Loan
                null                            // Home value
        );

        // WHEN
        MortgageCheckResponseDTO result = homeValueCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isFalse();
    }

    @Test
    @DisplayName("Check eligibility with null loan value")
    void shouldReturnIneligibleWhenCheckEligibilityWithNullLoanValue() {
        // GIVEN
        String reason = "Home value and/or loan value is null. Home value: 350000, Loan value: null";
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),
                360,
                null,                   // Loan
                new BigDecimal("350000")     // Home value (greater than loan)
        );

        // WHEN
        MortgageCheckResponseDTO result = homeValueCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains(reason);
    }

    @Test
    @DisplayName("Check eligibility with negative home value")
    void shouldReturnIneligibleWhenCheckEligibilityWithNegativeHomeValue() {
        // GIVEN
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),
                360,
                new BigDecimal("300000"),   // Loan
                new BigDecimal("-1")         // Home value
        );

        // WHEN
        MortgageCheckResponseDTO result = homeValueCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isFalse();
    }

    @Test
    @DisplayName("Check eligibility with negative loan value")
    void shouldReturnIneligibleWhenCheckEligibilityWithNegativeLoanValue() {
        // GIVEN
        String reason = "Loan value should be greater than Zero. Loan value: -1";
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),
                360,
                new BigDecimal("-1"),                   // Loan
                new BigDecimal("350000")     // Home value (greater than loan)
        );

        // WHEN
        MortgageCheckResponseDTO result = homeValueCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains(reason);
    }

    @Test
    @DisplayName("Check eligibility home value less than loan value")
    void shouldReturnIneligibleWhenCheckEligibilityWithLessHomeValue() {
        // GIVEN
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),
                360,
                new BigDecimal("300000"),   // Loan
                new BigDecimal("1000")       // Home value
        );

        // WHEN
        MortgageCheckResponseDTO result = homeValueCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("less than loan value");

    }
}
