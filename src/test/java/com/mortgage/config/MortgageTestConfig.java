package com.mortgage.config;

import com.mortgage.service.MortgageService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test Config class for Mortgage Service.
 */
@TestConfiguration
public class MortgageTestConfig {

    @Bean
    MortgageService mortgageService() {
        // return a mock or fake implementation
        return Mockito.mock(MortgageService.class);
    }
}