package com.mortgage.controller;

import com.mortgage.dto.MortgageRateDTO;
import com.mortgage.service.MortgageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MortgageControllerUnitTest {

    @Mock
    private MortgageService mortgageService;

    @InjectMocks
    private MortgageController mortgageController;

    @Test
    void shouldReturnInterestRatesWhenFetInterestRates() {
        var rates = List.of(new MortgageRateDTO(120, BigDecimal.valueOf(3.5), Instant.now(), "FIXED"));
        when(mortgageService.getInterestRates()).thenReturn(rates);

        var response = mortgageController.getInterestRates();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }
}

