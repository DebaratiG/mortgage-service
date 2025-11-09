package com.mortgage.loader;

import com.mortgage.dto.InterestTypeEnum;
import com.mortgage.entity.MortgageRateEntity;
import com.mortgage.repository.MortgageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * The MortgageRatesInitializer class is a component responsible for
 * initializing mortgage rates for 1-30 years terms in months when the application starts.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MortgageRatesInitializer implements CommandLineRunner {

    private final MortgageRepository mortgageRepository;

    @Override
    public void run(String... args) {
        if (mortgageRepository.findAll().isEmpty()) {
            log.info("Loading Interest Rates on application startup");

            var mortgageRateList = createMortgageRates(Instant.now());

            var count = mortgageRepository.saveAll(mortgageRateList);
            log.info("Successfully loaded {} interest rates into database", count);
        } else {
            log.info("Interest rates already exist in database. Skipping initialization.");
        }
    }

    /**
     * Creates a list of predefined mortgage rates for various loan terms.
     *
     * @param timestamp The timestamp indicating when the rates were last updated.
     *
     * @return A list of MortgageRateEntity instances representing mortgage rates
     *         for terms ranging from 5 years to 30 years.
     */
    private List<MortgageRateEntity> createMortgageRates(Instant timestamp) {

        return List.of(
                // Short-term fixed rates
                createRate(60, "4.25", timestamp, InterestTypeEnum.FIXED.name()),   // 5 years
                createRate(84, "4.50", timestamp, InterestTypeEnum.FIXED.name()),   // 7 years
                createRate(120, "4.75", timestamp, InterestTypeEnum.FIXED.name()),  // 10 years

                // Long-term variable rates (12-30 years)
                createRate(144, "4.90", timestamp, InterestTypeEnum.VARIABLE.name()),  // 12 years
                createRate(180, "5.00", timestamp, InterestTypeEnum.VARIABLE.name()),  // 15 years
                createRate(240, "5.25", timestamp, InterestTypeEnum.VARIABLE.name()),  // 20 years
                createRate(300, "5.50", timestamp, InterestTypeEnum.VARIABLE.name()),  // 25 years
                createRate(360, "5.75", timestamp, InterestTypeEnum.VARIABLE.name())   // 30 years
        );
    }

    /**
     *
     * Factory method to create a MortgageRateEntity with the specified parameters.
     *
     * @param maturityPeriod The loan term in months
     * @param rate The interest rate as a string
     * @param timestamp The last update timestamp+
     * @param type The InterestType Fixed or Variable
     *
     * @return A new MortgageRateEntity instance
     *
     */
    private MortgageRateEntity createRate(int maturityPeriod, String rate, Instant timestamp, String type) {

        return MortgageRateEntity.builder()
                .maturityPeriod(maturityPeriod)
                .interestRate(new BigDecimal(rate))
                .lastUpdated(timestamp)
                .type(type)
                .build();

    }
}
