package com.mortgage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mortgage.config.MortgageTestConfig;
import com.mortgage.dto.InterestTypeEnum;
import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import com.mortgage.dto.MortgageRateDTO;
import com.mortgage.exception.GlobalExceptionHandler;
import com.mortgage.exception.MortgageEligibilityFailedException;
import com.mortgage.service.MortgageService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MortgageController.class)
@Import({MortgageTestConfig.class, GlobalExceptionHandler.class})
class MortgageControllerTest {

    private static final String BASE_URL = "/api";
    private static final String MORTGAGE_CHECK_URL = BASE_URL + "/v1/mortgage-check";
    private static final String INTEREST_RATES_URL = BASE_URL + "/v1/interest-rates";

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

        rateDTO = new MortgageRateDTO(120, BigDecimal.valueOf(3.5), Instant.now(), InterestTypeEnum.FIXED.name() );
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return list of interest rates successfully")
    void shouldReturnInterestRatesListWhenGetInterestRates() {
        when(mortgageService.getInterestRates()).thenReturn(List.of(rateDTO));

        mockMvc.perform(get(INTEREST_RATES_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maturityPeriod").value(120))
                .andExpect(jsonPath("$[0].interestRate").value(3.5));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return 200 with empty response body when no interest rates found")
    void shouldReturnEmptyListWhenNoInterestRates() {
        when(mortgageService.getInterestRates()).thenReturn(List.of());

        mockMvc.perform(get(INTEREST_RATES_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should check mortgage eligibility successfully")
    void shouldCheckMortgageEligibilitySuccessfully() {
        when(mortgageService.checkEligibility(any(MortgageCheckRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post(MORTGAGE_CHECK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.monthlyCosts").value(4500))
                .andExpect(jsonPath("$.reason").value("All eligibility checks passed. Mortgage approved."));
    }

    @SneakyThrows
    @Test
    void shouldReturn400WhenTestCheckEligibilityWithEligibilityCheckFailedDueToIncorrectRequestData() {
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                BigDecimal.valueOf(50000),
                99,
                BigDecimal.valueOf(200000),
                BigDecimal.valueOf(250000)
        );

        when(mortgageService.checkEligibility(request))
                .thenThrow(new MortgageEligibilityFailedException("Interest rate not found for maturity period: 99"));

        mockMvc.perform(post(MORTGAGE_CHECK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Interest rate not found for maturity period: 99"))
                .andExpect(jsonPath("$.cause").value("Bad Request"))
                .andExpect(jsonPath("$.path").value(MORTGAGE_CHECK_URL));
    }

    @SneakyThrows
    @Test
    void shouldReturn422WhenTestCheckEligibilityWithValidationErrors() {
        // income is negative, violates @Valid constraints
        MortgageCheckRequestDTO invalidRequest = new MortgageCheckRequestDTO(
                BigDecimal.valueOf(-1000),
                12,
                BigDecimal.valueOf(200000),
                BigDecimal.valueOf(250000)
        );

        mockMvc.perform(post(MORTGAGE_CHECK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.cause").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("income")));
    }

    @SneakyThrows
    @Test
    void ShouldReturn400WhenTestCheckEligibilityWithMalformedJson() {
        // Missing closing brace to simulate malformed JSON
        String malformedJson = "{\"income\":50000,\"maturityPeriod\":12,\"loanValue\":200000";

        mockMvc.perform(post(MORTGAGE_CHECK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cause").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request format. Please check your JSON syntax and data types."))
                .andExpect(jsonPath("$.path").value(MORTGAGE_CHECK_URL));
    }

    @SneakyThrows
    @Test
    void shouldReturn500WhenTestCheckEligibilityWithRuntimeException() {
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                BigDecimal.valueOf(50000),
                12,
                BigDecimal.valueOf(200000),
                BigDecimal.valueOf(250000)
        );

        // Simulate unexpected RuntimeException from service
        when(mortgageService.checkEligibility(request))
                .thenThrow(new RuntimeException("Unexpected failure in service"));

        mockMvc.perform(post(MORTGAGE_CHECK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.cause").value("Internal Server Error"))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString("Unexpected failure in service")))
                .andExpect(jsonPath("$.path").value(MORTGAGE_CHECK_URL));
    }

}
