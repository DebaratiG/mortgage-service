package com.mortgage.service;

import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import com.mortgage.exception.MortgageRateNotFoundException;
import com.mortgage.rule.MortgageEligibilityCheckStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * This Service class is responsible for invoking the appropriate mortgage eligibility check strategy.
 * A List of strategies is injected as a dependency so that any new eligibility check strategy can be added without making
 * any changes to the service layer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MortgageStrategyService {

    private final List<MortgageEligibilityCheckStrategy> strategies;

    /**
     *
     * Evaluates the eligibility of a mortgage application based on the requested income amount and home value.
     * The method applies all registered mortgage eligibility check strategies and returns the results.
     *
     * @param requestDTO MortgageCheckRequestDTO mortgage request object containing the applicant's financial details.
     *
     * @return MortgageCheckResponseDTO object containing the eligibility status, calculated
     */
    public MortgageCheckResponseDTO loanValidation(MortgageCheckRequestDTO requestDTO) {

        var responses = strategies.stream()
                .map(strategy -> strategy.checkEligibility(requestDTO))
                .toList(); // Java 16+ immutable List

        if (responses.stream().anyMatch(r -> !r.isEligible())) {
            // Optionally collect which ones failed
            var failed = responses.stream()
                    .filter(r -> !r.isEligible())
                    .map(MortgageCheckResponseDTO::getReason)
                    .toList();
            log.error("Eligibility failed for: {}", String.join(", ", failed));
            throw new MortgageRateNotFoundException("Eligibility failed for: " + String.join(", ", failed));
        }

        return new MortgageCheckResponseDTO(true, BigDecimal.ZERO, "All eligibility checks passed");
    }
}
