package com.mortgage.rule;

import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;

/**
 * Interface for defining strategies to check mortgage eligibility and affordability.
 * <p>
 * This interface provides a method to determine the eligibility of a mortgage
 * application based on specific business rules or criteria. Implementing
 * classes must provide the logic for evaluating the mortgage request and
 * returning the results encapsulated in a {@link MortgageCheckResponseDTO}.
 * <p>
 * The primary purpose is to enable modularity and flexibility by allowing
 * different eligibility-checking strategies to be implemented and used
 * interchangeably.
 * <p>
 * Implementing classes might evaluate different aspects such as
 *          1. Applicant's income.
 *          2. Value of the home.
 * <p>
 * This promotes the strategy design pattern, enabling business logic for
 * mortgage eligibility to be dynamically configured or switched based on
 * use cases.
 */
public interface MortgageEligibilityCheckStrategy {
    MortgageCheckResponseDTO checkEligibility(MortgageCheckRequestDTO mortgageCheckRequestDTO);
}
