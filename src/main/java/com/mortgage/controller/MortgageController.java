package com.mortgage.controller;

import com.mortgage.dto.MortgageCheckRequestDTO;
import com.mortgage.dto.MortgageCheckResponseDTO;
import com.mortgage.dto.MortgageRateDTO;
import com.mortgage.exception.MortgageApiError;
import com.mortgage.service.MortgageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller provides mortgage-related operations where
 * the endpoints are retrieving current interest rates against terms in months
 * and checking mortgage eligibility for loan applications.
 *
 * @author Debarati Ghosh
 * @version 1.0
 * @since 2025-11-07
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Mortgage API", description = "APIs for mortgage interest rates and eligibility checks")
public class MortgageController {

    private final MortgageService mortgageService;

    /**
     * Retrieves the list of current mortgage interest rates.
     * <p>
     * This endpoint returns the current interest rates available for different
     * maturity periods. Maturity Period is expressed in months.
     * </p>
     *
     * @return ResponseEntity containing a list of Interest Rates
     */
    @Operation(
            summary = "Get current interest rates",
            description = "Retrieves the current mortgage interest rates for different maturity periods."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved interest rates",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MortgageRateDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - An unexpected error occurred while retrieving interest rates",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MortgageApiError.class)
                    )
            )
    })
    @GetMapping("/v1/interest-rates")
    public List<MortgageRateDTO> getInterestRates() {
        log.info("Start retrieving interest rates");
        return mortgageService.getInterestRates();
    }


    /**
     * Evaluates the mortgage eligibility of an applicant based on their financial details,
     * such as income, requested loan amount, home value, and the requested maturity period.
     *
     * @param requestDTO the request payload containing the applicant's financial information
     *                   (income, loan amount, home value, maturity period, etc.)
     *
     * @return a {@link ResponseEntity} containing a {@link MortgageCheckResponseDTO} object
     *         that includes the eligibility result, estimated monthly costs, and additional comments.
     */

    @Operation(
            summary = "Check mortgage eligibility",
            description = "Evaluates mortgage eligibility based on applicant's financial information including " +
                    "income, loan value, and home value for the given maturity period."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully processed mortgage eligibility check",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MortgageCheckResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input format or missing required fields",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MortgageApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity - Validation failed. Request contains invalid values (e.g., negative amounts, invalid maturity period)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MortgageApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error - An unexpected error occurred while processing the request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MortgageApiError.class)
                    )
            )
    })
    @PostMapping("/v1/mortgage-check")
    public MortgageCheckResponseDTO checkMortgageEligibility(
            @Parameter(
                    description = "Mortgage check request with applicant's financial information",
                    required = true,
                    schema = @Schema(implementation = MortgageCheckRequestDTO.class)
            )
            @Valid @RequestBody MortgageCheckRequestDTO requestDTO
    ) {
        log.info("Mortgage check request: {}", requestDTO);
        return mortgageService.checkEligibility(requestDTO);
    }
}
