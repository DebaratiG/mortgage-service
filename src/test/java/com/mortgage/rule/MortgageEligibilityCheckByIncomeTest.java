package com.mortgage.rule;

import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import com.mortgage.service.MortgageMonthlyCostCalculatorHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Income Eligibility Strategy Tests")
class MortgageEligibilityCheckByIncomeTest {

    @Mock
    private MortgageMonthlyCostCalculatorHelper monthlyCostCalculator;

    @InjectMocks
    private MortgageEligibilityCheckByIncome incomeCheck;

    @Test
    @DisplayName("Check eligibility where mortgage should be within 4x income limit")
    void shouldReturnEligibleWhenCheckEligibilityWithinFourTimesIncomeLimit() {
        // GIVEN
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),  // Income
                360,
                new BigDecimal("300000"),  // Loan (3x income)
                new BigDecimal("350000")
        );

        // WHEN
        MortgageCheckResponseDTO result = incomeCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isTrue();
        assertThat(result.getReason()).contains("passed");
    }

    @Test
    @DisplayName("Check eligibility where loan value is null")
    void shouldReturnInEligibleWhenCheckEligibilityWithNullLoanValue() {
        // GIVEN
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),  // Income
                360,
                null,  // Loan (3x income)
                new BigDecimal("350000")
        );

        // WHEN
        MortgageCheckResponseDTO result = incomeCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("loan value is null");
    }

    @Test
    @DisplayName("Check eligibility where mortgage exceeds 4x income limit")
    void shouldReturnIneligibleWhenCheckEligibilityExceedsIncomeLimit() {
        // GIVEN
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("50000"),   // Income
                360,
                new BigDecimal("300000"),  // Loan (6x income - exceeds limit)
                new BigDecimal("350000")
        );

        // WHEN
        MortgageCheckResponseDTO result = incomeCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("4 times");
        assertThat(result.getReason()).contains("200000");
    }

    @Test
    @DisplayName("Check eligibility when mortgage is exactly at 4x income limit")
    void shouldReturnEligibleWhenCheckEligibilityIsExactlyAtLimit() {
        // GIVEN
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),  // Income
                360,
                new BigDecimal("400000"),  // Loan (exactly 4x income)
                new BigDecimal("450000")
        );

        // WHEN
        MortgageCheckResponseDTO result = incomeCheck.checkEligibility(request);

        // THEN
        assertThat(result.isEligible()).isTrue();
    }
}
