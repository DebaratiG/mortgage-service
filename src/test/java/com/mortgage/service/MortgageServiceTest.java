package com.mortgage.service;

import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import com.mortgage.dto.MortgageRateDTO;
import com.mortgage.entity.MortgageRateEntity;
import com.mortgage.exception.MortgageEligibilityFailedException;
import com.mortgage.mapper.MortgageRateMapper;
import com.mortgage.repository.MortgageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MortgageServiceTest {

    @Mock
    private MortgageRepository mortgageRepository;

    @Mock
    private MortgageRateMapper mortgageRateMapper;

    @Mock
    private MortgageStrategyService strategyService;

    @Mock
    private MortgageMonthlyCostCalculatorHelper monthlyCostCalculator;

    @InjectMocks
    private MortgageService mortgageService;

    private MortgageRateEntity rateEntity;
    private MortgageRateDTO rateDTO;
    private MortgageCheckRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        rateEntity = new MortgageRateEntity();
        rateEntity.setId(1L);
        rateEntity.setMaturityPeriod(10);
        rateEntity.setInterestRate(BigDecimal.valueOf(5.5));

        rateDTO = new MortgageRateDTO(10, BigDecimal.valueOf(4.5), Instant.now(), "VARIABLE");

        requestDTO =  new MortgageCheckRequestDTO(BigDecimal.valueOf(80000), 10, BigDecimal.valueOf(500000),
                BigDecimal.valueOf(1000000));
    }

    @Test
    @DisplayName("Should return sorted list of interest rates")
    void shouldReturnSortedOrderedInterestRates() {
        // GIVEN
        when(mortgageRepository.findAll(Sort.by(Sort.Direction.ASC, "maturityPeriod")))
                .thenReturn(List.of(rateEntity));
        when(mortgageRateMapper.toDTO(rateEntity)).thenReturn(rateDTO);

        // WHEN
        var result = mortgageService.getInterestRates();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().maturityPeriod()).isEqualTo(10);
        verify(mortgageRepository).findAll(Sort.by(Sort.Direction.ASC, "maturityPeriod"));
    }

    @Test
    @DisplayName("Should return approved eligibility when strategy is eligible")
    void shouldReturnApprovedEligibilityWhenEligibleStrategyIsTrue() {
        // GIVEN
        when(mortgageRepository.findByMaturityPeriod(10)).thenReturn(Optional.of(rateEntity));

        var eligibleResponse = new MortgageCheckResponseDTO(true, BigDecimal.ZERO, "Eligible");
        when(strategyService.loanValidation(requestDTO)).thenReturn(eligibleResponse);
        when(monthlyCostCalculator.calculateMonthlyCost(BigDecimal.valueOf(500000),
                BigDecimal.valueOf(5.5), 10)).thenReturn(BigDecimal.valueOf(5500));

        // WHEN
        var response = mortgageService.checkEligibility(requestDTO);

        // THEN
        assertThat(response.isEligible()).isTrue();
        assertThat(response.getReason()).contains("Mortgage approved");
        assertThat(response.getMonthlyCosts()).isEqualTo(BigDecimal.valueOf(5500));
    }

    @Test
    @DisplayName("Should return ineligible response when strategy fails")
    void shouldReturnIneligibleResponseWhenEligibleStrategyFails() {
        // GIVEN
        when(mortgageRepository.findByMaturityPeriod(10)).thenReturn(Optional.of(rateEntity));

        var ineligibleResponse = new MortgageCheckResponseDTO(false, BigDecimal.ZERO, "Low income");
        when(strategyService.loanValidation(requestDTO)).thenReturn(ineligibleResponse);

        // WHEN
        var response = mortgageService.checkEligibility(requestDTO);

        // THEN
        assertThat(response.isEligible()).isFalse();
        assertThat(response.getReason()).isEqualTo("Low income");
        verify(monthlyCostCalculator, never()).calculateMonthlyCost(any(), any(), anyInt());
    }

    @Test
    @DisplayName("Should throw MortgageEligibilityFailedException when no rate found")
    void shouldThrowRateNotFoundWhenCheckEligible() {
        // WHEN
        when(mortgageRepository.findByMaturityPeriod(10)).thenReturn(Optional.empty());

        // THEN
        assertThatThrownBy(() -> mortgageService.checkEligibility(requestDTO))
                .isInstanceOf(MortgageEligibilityFailedException.class)
                .hasMessageContaining("Interest rate not found for maturity period: 10");
    }
}
