package com.mortgage.rule;

import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * MortgageEligibilityCheckByHomeValue evaluates the eligibility of a mortgage applicant
 * based on the value of the property (home) associated with the loan.
 * <p>
 * Implementing this strategy ensures that the loan-to-value (LTV) ratio is considered,
 * thereby promoting responsible lending practices.
 */
@Component
@Slf4j
public class MortgageEligibilityCheckByHomeValue implements MortgageEligibilityCheckStrategy{

    /**
     * This method applies a business rule ensuring the requested loan amount does not exceed the value
     * of the home. If the value of the home is less than the requested loan amount, the
     * applicant is deemed ineligible.
     * @param requestDTO MortgageCheckRequestDTO
     *                   mortgage request object containing the applicant's financial details.
     *
     * @return MortgageCheckResponseDTO object containing the eligibility status, calculated
     */
    @Override
    public MortgageCheckResponseDTO checkEligibility(MortgageCheckRequestDTO requestDTO) {
        var homeValue = requestDTO.homeValue();
        var loanValue = requestDTO.loanValue();

        if (homeValue == null || loanValue == null) {
            log.warn("Home value and/or loan value is null. Home value: {}, Loan value: {}", homeValue, loanValue);
            return new MortgageCheckResponseDTO(false,
                    BigDecimal.ZERO,
                    "Home value and/or loan value is null. Home value: " + homeValue + ", Loan value: " + loanValue);

        } else if (homeValue.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Home value should be greater than Zero. Home value: {}", homeValue);
            return new MortgageCheckResponseDTO(false,
                    BigDecimal.ZERO,
                    "Home value should be greater than Zero. Home value: " + homeValue);

        } else if (loanValue.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Loan value should be greater than Zero. Loan value: {}", loanValue);
            return new MortgageCheckResponseDTO(false,
                    BigDecimal.ZERO,
                    "Loan value should be greater than Zero. Loan value: " + loanValue);

        } else if (homeValue.compareTo(loanValue) >= 0) {
            log.info("Loan Value eligibility check passed.");
            return new MortgageCheckResponseDTO(true,
                    BigDecimal.ZERO,
                    "Loan Value eligibility check passed.");

        } else if (homeValue.compareTo(loanValue) == 0) {
            log.info("Loan Value and Home value eligibility check passed.");
            return new MortgageCheckResponseDTO(true,
                    BigDecimal.ZERO,
                    "Loan Value and Home value eligibility check passed.");
        } else {
            log.warn("Home value is less than loan value. Home value: {}, Loan value: {}", homeValue, loanValue);
            return new MortgageCheckResponseDTO(
                    false,
                    BigDecimal.ZERO,
                    "Home value is less than loan value. Home value: " + homeValue + ", Loan value: " + loanValue);
        }
    }
}
