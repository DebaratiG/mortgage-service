package com.mortgage.repository;

import com.mortgage.entity.MortgageRateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("Mortgage Repository Tests")
class MortgageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MortgageRepository mortgageRepository;

    private MortgageRateEntity rateEntity;

    @BeforeEach
    void setUp() {
        rateEntity = new MortgageRateEntity();
        rateEntity.setMaturityPeriod(360);
        rateEntity.setInterestRate(new BigDecimal("4.5"));
        rateEntity.setLastUpdated(Instant.now());
        rateEntity.setType("VARIABLE");
    }

    @Test
    @DisplayName("Find by maturity period is successful")
    void shouldReturnEntityWhenFindByMaturityPeriodForExistingPeriod() {
        // GIVEN
        entityManager.persist(rateEntity);
        entityManager.flush();

        // WHEN
        Optional<MortgageRateEntity> result = mortgageRepository.findByMaturityPeriod(360);

        // THEN
        assertThat(result.isPresent());
        assertThat(result.get().interestRate).isEqualByComparingTo(new BigDecimal("4.5"));
    }

    @Test
    @DisplayName("Find by maturity period shows not found")
    void shouldReturnEmptyWhenFindByMaturityPeriodIsNonExistingPeriod() {
        // WHEN
        Optional<MortgageRateEntity> result = mortgageRepository.findByMaturityPeriod(999);

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Save mortgage rate is success")
    void shouldSaveMortgageRateWithValidEntity() {
        // WHEN
        MortgageRateEntity saved = mortgageRepository.save(rateEntity);

        // THEN
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMaturityPeriod()).isEqualTo(360);
        assertThat(saved.getInterestRate()).isEqualByComparingTo(new BigDecimal("4.5"));
    }

    @Test
    @DisplayName(" Returns Sorted List")
    void shouldReturnSortedListWhenFindAllMultiplePeriods() {
        // GIVEN
        MortgageRateEntity rate1 = new MortgageRateEntity();
        rate1.setMaturityPeriod(60);
        rate1.setInterestRate(new BigDecimal("3.5"));
        rate1.setLastUpdated(Instant.now());
        rate1.setType("FIXED");

        MortgageRateEntity rate2 = new MortgageRateEntity();
        rate2.setMaturityPeriod(240);
        rate2.setInterestRate(new BigDecimal("4.0"));
        rate2.setLastUpdated(Instant.now());
        rate2.setType("VARIABLE");

        entityManager.persist(rate2);
        entityManager.persist(rate1);
        entityManager.flush();

        // WHEN
        var result = mortgageRepository.findAll();

        // THEN
        assertThat(result).hasSize(2);
    }
}