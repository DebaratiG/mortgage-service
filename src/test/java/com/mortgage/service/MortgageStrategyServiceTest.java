package com.mortgage.service;

import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import com.mortgage.exception.MortgageRateNotFoundException;
import com.mortgage.rule.MortgageEligibilityCheckStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MortgageStrategyServiceTest {

    @Mock
    private MortgageEligibilityCheckStrategy incomeStrategy;

    @Mock
    private MortgageEligibilityCheckStrategy creditScoreStrategy;

    @InjectMocks
    private MortgageStrategyService mortgageStrategyService;

    private MortgageCheckRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new MortgageCheckRequestDTO(BigDecimal.valueOf(500000), 240, BigDecimal.valueOf(1500000), BigDecimal.valueOf(1600000));
        mortgageStrategyService = new MortgageStrategyService(List.of(incomeStrategy, creditScoreStrategy));
    }

    @Test
    @DisplayName("Should return eligible when all strategies return true")
    void shouldReturnEligibleWhenAllStrategiesPass() {
        // GIVEN
        var eligibleResponse = new MortgageCheckResponseDTO(true, BigDecimal.ZERO, "Eligible");
        when(incomeStrategy.checkEligibility(requestDTO)).thenReturn(eligibleResponse);
        when(creditScoreStrategy.checkEligibility(requestDTO)).thenReturn(eligibleResponse);

        // WHEN
        var result = mortgageStrategyService.loanValidation(requestDTO);

        // THEN
        assertThat(result.isEligible()).isTrue();
        assertThat(result.getReason()).isEqualTo("All eligibility checks passed");
    }

    @Test
    @DisplayName("Should throw exception when any strategy fails")
    void shouldThrowExceptionWhenAnyStrategyFails() {
        // GIVEN
        var eligibleResponse = new MortgageCheckResponseDTO(true, BigDecimal.ZERO, "Eligible");
        var ineligibleResponse = new MortgageCheckResponseDTO(false, BigDecimal.ZERO, "Low credit score");

        when(incomeStrategy.checkEligibility(requestDTO)).thenReturn(eligibleResponse);
        when(creditScoreStrategy.checkEligibility(requestDTO)).thenReturn(ineligibleResponse);

        // THEM
        assertThatThrownBy(() -> mortgageStrategyService.loanValidation(requestDTO))
                .isInstanceOf(MortgageRateNotFoundException.class)
                .hasMessageContaining("Eligibility failed for: Low credit score");
    }
}
