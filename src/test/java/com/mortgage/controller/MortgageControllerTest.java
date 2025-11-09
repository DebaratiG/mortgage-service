package com.mortgage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mortgage.config.MortgageTestConfig;
import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import com.mortgage.dto.MortgageRateDTO;
import com.mortgage.service.MortgageService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MortgageController.class)
@Import(MortgageTestConfig.class)
class MortgageControllerTest {

    private static final String BASE_URL = "/v1/api";
    private static final String MORTGAGE_CHECK_URL = BASE_URL + "/mortgage-check";
    private static final String INTEREST_RATES_URL = BASE_URL + "/interest-rates";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MortgageService mortgageService;

    @Autowired
    private ObjectMapper objectMapper;

    private MortgageCheckRequestDTO requestDTO;
    private MortgageCheckResponseDTO responseDTO;
    private MortgageRateDTO rateDTO;

    @BeforeEach
    void setUp() {

        requestDTO = new MortgageCheckRequestDTO(
                BigDecimal.valueOf(500000),
                10,
                BigDecimal.valueOf(1000000),
                BigDecimal.valueOf(80000));


        responseDTO = new MortgageCheckResponseDTO(
                true,
                BigDecimal.valueOf(4500),
                "All eligibility checks passed. Mortgage approved."
        );

        rateDTO = new MortgageRateDTO(120, BigDecimal.valueOf(3.5), Instant.now(), "FIXED" );
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return list of interest rates successfully")
    void shouldReturnInterestRatesListWhenGetInterestRates() {
        Mockito.when(mortgageService.getInterestRates()).thenReturn(List.of(rateDTO));

        mockMvc.perform(get(INTEREST_RATES_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maturityPeriod").value(120))
                .andExpect(jsonPath("$[0].interestRate").value(3.5));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return 404 when no interest rates found")
    void shouldReturnNotFoundWhenNoInterestRates() {
        Mockito.when(mortgageService.getInterestRates()).thenReturn(List.of());

        mockMvc.perform(get(INTEREST_RATES_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    @DisplayName("Should check mortgage eligibility successfully")
    void shouldCheckMortgageEligibilitySuccessfully() {
        Mockito.when(mortgageService.checkEligibility(any(MortgageCheckRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post(MORTGAGE_CHECK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.monthlyCosts").value(4500))
                .andExpect(jsonPath("$.reason").value("All eligibility checks passed. Mortgage approved."));
    }
}
