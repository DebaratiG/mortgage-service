package com.mortgage.loader;

import com.mortgage.entity.MortgageRateEntity;
import com.mortgage.repository.MortgageRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MortgageRatesInitializer class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MortgageRatesInitializer Tests")
class MortgageRatesInitializerTest {

    @Mock
    private MortgageRepository mortgageRepository;

    @InjectMocks
    private MortgageRatesInitializer mortgageRatesInitializer;

    @Captor
    private ArgumentCaptor<List<MortgageRateEntity>> ratesCaptor;

    @BeforeEach
    void setUp() {
        // Reset mock interactions before each test
        reset(mortgageRepository);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should initialize rates when database is empty")
    void shouldInitializeRatesWhenDatabaseIsEmpty() {
        // Given
        when(mortgageRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        mortgageRatesInitializer.run();

        // Then
        verify(mortgageRepository, times(1)).findAll();
        verify(mortgageRepository, times(1)).saveAll(ratesCaptor.capture());

        var savedRates = ratesCaptor.getValue();
        assertThat(savedRates)
            .isNotNull()
            .hasSize(8)
            .allMatch(rate -> rate.getMaturityPeriod() != null)
            .allMatch(rate -> rate.getInterestRate() != null)
            .allMatch(rate -> rate.getLastUpdated() != null);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should save all 8 predefined mortgage rates")
    void shouldSaveAllPredefinedRates() {
        // Given
        when(mortgageRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        mortgageRatesInitializer.run();

        // Then
        verify(mortgageRepository).saveAll(ratesCaptor.capture());

        var savedRates = ratesCaptor.getValue();
        
        // Verify expected maturity periods (in months)
        var expectedMaturityPeriods = List.of(
            60, 84, 120, 144, 180,
            240, 300, 360
        );

        var actualMaturityPeriods = savedRates.stream()
            .map(MortgageRateEntity::getMaturityPeriod)
            .sorted()
            .toList();

        assertThat(actualMaturityPeriods).containsExactlyElementsOf(expectedMaturityPeriods);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should create rates with correct interest rate values")
    void shouldCreateRatesWithCorrectInterestRates() {
        // Given
        when(mortgageRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        mortgageRatesInitializer.run();

        // Then
        verify(mortgageRepository).saveAll(ratesCaptor.capture());

        var savedRates = ratesCaptor.getValue();

        // Verify specific rate values
        assertThat(savedRates)
            .extracting(MortgageRateEntity::getMaturityPeriod, MortgageRateEntity::getInterestRate)
            .contains(
                tuple(60, new BigDecimal("4.25")),   // 5 years
                tuple(84, new BigDecimal("4.50")),   // 7 years
                tuple(120, new BigDecimal("4.75")),  // 10 years
                tuple(180, new BigDecimal("5.00")),  // 15 years
                tuple(360, new BigDecimal("5.75"))   // 30 years
            );
    }

    @SneakyThrows
    @Test
    @DisplayName("Should verify interest rates increase with maturity period")
    void shouldVerifyInterestRatesIncreaseWithMaturity() {
        // Given
        when(mortgageRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        mortgageRatesInitializer.run();

        // Then
        verify(mortgageRepository).saveAll(ratesCaptor.capture());

        var savedRates = ratesCaptor.getValue();
        var sortedRates = savedRates.stream()
            .sorted((r1, r2) -> r1.getMaturityPeriod().compareTo(r2.getMaturityPeriod()))
            .toList();

        // Verify that interest rates generally increase with a maturity period
        for (int i = 0; i < sortedRates.size() - 1; i++) {
            var currentRate = sortedRates.get(i).getInterestRate();
            var nextRate = sortedRates.get(i + 1).getInterestRate();
            
            assertThat(nextRate)
                .as("Interest rate should increase or stay same for longer maturity periods")
                .isGreaterThanOrEqualTo(currentRate);
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("Should not initialize rates when database already has data")
    void shouldNotInitializeRatesWhenDatabaseHasData() {
        // Given
        var existingRate = new MortgageRateEntity(
            1L, 
            60,
            new BigDecimal("3.50"), 
            java.time.Instant.now(),
                "FIXED"
        );
        when(mortgageRepository.findAll()).thenReturn(List.of(existingRate));

        // When
        mortgageRatesInitializer.run();

        // Then
        verify(mortgageRepository, times(1)).findAll();
        verify(mortgageRepository, never()).saveAll(any());
    }

    @SneakyThrows
    @Test
    @DisplayName("Should verify all rates have valid BigDecimal precision")
    void shouldVerifyAllRatesHaveValidBigDecimalPrecision() {
        // Given
        when(mortgageRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        mortgageRatesInitializer.run();

        // Then
        verify(mortgageRepository).saveAll(ratesCaptor.capture());

        var savedRates = ratesCaptor.getValue();

        // Verify all interest rates have the appropriate scale (2 decimal places)
        assertThat(savedRates)
            .allMatch(rate -> rate.getInterestRate().scale() == 2,
                "All interest rates should have 2 decimal places");

        // Verify all interest rates are positive
        assertThat(savedRates)
            .allMatch(rate -> rate.getInterestRate().compareTo(BigDecimal.ZERO) > 0,
                "All interest rates should be positive");
    }

    @SneakyThrows
    @Test
    @DisplayName("Should verify maturity periods cover standard mortgage terms")
    void shouldVerifyMaturityPeriodsCoverStandardTerms() {
        // Given
        when(mortgageRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        mortgageRatesInitializer.run();

        // Then
        verify(mortgageRepository).saveAll(ratesCaptor.capture());

        var savedRates = ratesCaptor.getValue();
        var maturityPeriods = savedRates.stream()
            .map(MortgageRateEntity::getMaturityPeriod)
            .toList();

        // Verify common mortgage terms are present
        assertThat(maturityPeriods)
            .as("Should include 5year term")
            .contains(60);
        
        assertThat(maturityPeriods)
            .as("Should include 10year term")
            .contains(120);
        
        assertThat(maturityPeriods)
            .as("Should include 15year term")
            .contains(180);
        
        assertThat(maturityPeriods)
            .as("Should include 30year term")
            .contains(360);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should set timestamp for all initialized rates")
    void shouldSetTimestampForAllRates() {
        // Given
        when(mortgageRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        mortgageRatesInitializer.run();

        // Then
        verify(mortgageRepository).saveAll(ratesCaptor.capture());

        var savedRates = ratesCaptor.getValue();

        // Verify all rates have a timestamp
        assertThat(savedRates)
            .allMatch(rate -> rate.getLastUpdated() != null,
                "All rates should have a lastUpdate timestamp");

        // Verify all timestamps are recent (within the last minute)
        var now = java.time.Instant.now();
        assertThat(savedRates)
            .allMatch(rate -> rate.getLastUpdated().isBefore(now.plusSeconds(60)),
                "All timestamps should be recent");
    }

    @SneakyThrows
    @Test
    @DisplayName("Should create rates with null IDs for database generation")
    void shouldCreateRatesWithNullIds() {
        // Given
        when(mortgageRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        mortgageRatesInitializer.run();

        // Then
        verify(mortgageRepository).saveAll(ratesCaptor.capture());

        var savedRates = ratesCaptor.getValue();

        // Verify all rates have null IDs (to be generated by a database)
        assertThat(savedRates)
            .allMatch(rate -> rate.getId() == null,
                "All rate IDs should be null before saving to database");
    }

    /**
     * Helper method to create a tuple for AssertJ assertions.
     */
    private static org.assertj.core.groups.Tuple tuple(Object... values) {
        return org.assertj.core.api.Assertions.tuple(values);
    }
}
