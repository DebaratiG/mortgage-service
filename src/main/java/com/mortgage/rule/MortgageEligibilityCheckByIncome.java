package com.mortgage.rule;

import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * MortgageEligibilityCheckByIncome is an implementation of the MortgageEligibilityCheckStrategy interface.
 * It evaluates the eligibility of a mortgage applicant based on their annual income.
 * <p>
 * This class applies a specific rule where the requested loan amount cannot exceed four times
 * the applicant's annual income (Loan-to-Income Ratio limit). If the requested loan value
 * surpasses this threshold, the applicant is considered ineligible.
 *
 */
@Component
@Slf4j
public class MortgageEligibilityCheckByIncome implements MortgageEligibilityCheckStrategy {

    private static final BigDecimal MAX_LOAN_TO_INCOME_RATIO = BigDecimal.valueOf(4);

    /**
     * Evaluates the eligibility of a mortgage application based on income and the requested loan amount.
     * The method calculates the maximum allowable loan based on the applicant's annual income
     * and checks whether the requested loan exceeds this limit.
     * <p>
     * If the requested loan amount is within the allowable range, the application is determined to be eligible,
     * and the method calculates the monthly cost considering the maturity period. Otherwise, the application
     * is ineligible, and the reason for rejection is provided.
     *
     * @param requestDTO The mortgage request object containing the applicant's financial details,
     *                   including income, loan value, maturity period, and home value.
     *
     * @return A {@link MortgageCheckResponseDTO} containing the eligibility status, calculated
     *         monthly payment (if eligible), and the reason for rejection (if ineligible).
     */
    @Override
    public MortgageCheckResponseDTO checkEligibility(MortgageCheckRequestDTO requestDTO) {
        var income = requestDTO.income();
        var loanValue = requestDTO.loanValue();
        var maximumAllowedLoan = income.multiply(MAX_LOAN_TO_INCOME_RATIO);

        if (loanValue == null) {
            log.warn("Loan value is null. Income: {}", income);
            return new MortgageCheckResponseDTO(false,
                    BigDecimal.ZERO,
                    "Income and/or loan value is null. Income: " + income + ", Loan value: " + null);
        }
        //True when loan value is less than max allowed loan value.
        if (loanValue.compareTo(maximumAllowedLoan) <= 0) {
            log.info("Income eligibility check passed.");
            return new MortgageCheckResponseDTO(true,
                    BigDecimal.ZERO,
                    "Income eligibility check passed.");
        } else {
            return new MortgageCheckResponseDTO(
                    false,
                    BigDecimal.ZERO,
                    "Loan value exceeds 4 times the annual income. Maximum allowed loan: " + maximumAllowedLoan);

        }
    }
}
