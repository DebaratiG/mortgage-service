package com.mortgage.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mortgage.dto.MortgageCheckRequestDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Mortgage Service Integration Tests")
class MortgageServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    @DisplayName("End-to-End: Get interest rates and check eligibility")
    void shouldReturnSuccessWhenGetRatesAndCheckedEligibility() {
        // First, get interest rates
        mockMvc.perform(get("/v1/api/interest-rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Then, check eligibility
        MortgageCheckRequestDTO request = new MortgageCheckRequestDTO(
                new BigDecimal("100000"),
                360,
                new BigDecimal("300000"),
                new BigDecimal("350000")
        );

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").exists())
                .andExpect(jsonPath("$.monthlyCosts").exists())
                .andExpect(jsonPath("$.reason").exists());
    }
}
