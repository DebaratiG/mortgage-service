package com.mortgage.service;

import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import com.mortgage.dto.MortgageRateDTO;
import com.mortgage.entity.MortgageRateEntity;
import com.mortgage.exception.MortgageEligibilityFailedException;
import com.mortgage.mapper.MortgageRateMapper;
import com.mortgage.repository.MortgageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for managing mortgage-related operations.
 * <p>
 * This class provides functionalities to interact with mortgage data,
 * including retrieving mortgage rates.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MortgageService {

    private final MortgageRepository mortgageRepository;
    private final MortgageRateMapper mortgageRateMapper;
    private final MortgageStrategyService strategyService;
    private final MortgageMonthlyCostCalculatorHelper monthlyCostCalculator;


    /**
     * Retrieves a list of mortgage interest rates.
     * <p>
     * This method pulls all mortgage rate entities from the repository,
     * maps them into DTO representations, and returns them as a list.
     * <p>
     * Added @Cacheable annotation to enable caching of the results as
     * Interest rates don't change frequently.
     * <p>
     * The list is sorted by maturity period in ascending order.
     *
     * @return a list of {@link MortgageRateDTO} objects representing the
     *         interest rates for various loan maturity periods.
     */
    @Cacheable("interestRates")
    public List<MortgageRateDTO> getInterestRates() {
            return mortgageRepository.findAll(Sort.by(Sort.Direction.ASC, "maturityPeriod"))
                    .stream()
                    .map(mortgageRateMapper::toDTO)
                    .toList();
    }

    /**
     * Evaluates the eligibility of a mortgage application based on income, loan value,
     * home value.
     * <p>
     * This method performs multiple eligibility checks, such as income eligibility
     * and home value eligibility. If all checks pass, the applicant is determined
     * to be eligible for the requested mortgage. Otherwise, the reason for ineligibility
     * is provided. Additionally, it calculates the monthly costs for eligible
     * applications using the appropriate interest rate.
     *
     * @param mortgageCheckRequestDTO The mortgage application request containing
     *                                details such as income, maturity period,
     *                                loan value, and home value.
     *
     * @return A {@link MortgageCheckResponseDTO} object representing the result
     *         of the eligibility check. It includes the eligibility status
     *         (true or false), the calculated monthly costs if eligible, and
     *         a reason describing the decision.
     */
    public MortgageCheckResponseDTO checkEligibility(MortgageCheckRequestDTO mortgageCheckRequestDTO) {
        BigDecimal interestRate = getInterestRateByMaturityPeriod(mortgageCheckRequestDTO.maturityPeriod());

        // Pass the MortgageCheckRequestDTO to the strategy service to perform validation on both as Income and Home value
        MortgageCheckResponseDTO eligibilityCheck = strategyService.loanValidation(mortgageCheckRequestDTO);
        if (eligibilityCheck.isEligible()) {
            var monthlyCosts = monthlyCostCalculator.calculateMonthlyCost(
                    mortgageCheckRequestDTO.loanValue(), interestRate, mortgageCheckRequestDTO.maturityPeriod());

            // Return the combined result using pattern matching on eligibility
            return new MortgageCheckResponseDTO(
                    true,
                    monthlyCosts,
                    "All eligibility checks passed. Mortgage approved."
            );
        }
        log.info("Mortgage check completed: {}", eligibilityCheck);
        return eligibilityCheck;
    }

        /**
         * @param maturityPeriod the maturity period for which the interest rate is required
         *                       to find the mortgage eligibility.
         *
         * @return Big decimal interest rate for the given maturity period.
         */
    private BigDecimal getInterestRateByMaturityPeriod(Integer maturityPeriod) {
        return mortgageRepository.findByMaturityPeriod(maturityPeriod)
                .map(MortgageRateEntity::getInterestRate)
                .orElseThrow(() -> new MortgageEligibilityFailedException(
                        "Interest rate not found for maturity period: " + maturityPeriod
                ));
        }
    }
