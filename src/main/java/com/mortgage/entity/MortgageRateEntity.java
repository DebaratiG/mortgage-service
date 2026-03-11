package com.mortgage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entity representing the mortgage rate data.
 * <p>
 * This class is mapped to the `mortgage_rate` table in the database and
 * contains information related to mortgage rates for different loan terms.
 *
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name="mortgage_rates")
public class MortgageRateEntity {

    /**
     * Unique identifier for the mortgage rate record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /**
     * The duration of the mortgage term, typically expressed in months.
     */
    @Column(nullable = false, unique = true)
    public Integer maturityPeriod;

    /**
     * The annual interest rate applicable for the specified maturity period.
     */
    @Column(nullable = false, precision = 10, scale = 6)
    public BigDecimal interestRate;

    /**
     * The timestamp indicating the last update to the mortgage rate record.
     */
    @Column(nullable = false)
    @Builder.Default
    public Instant lastUpdated = Instant.now();

    /**
     * The type specifies FIXED or VARIABLE.
     */
    @Column(nullable = false)
    public String type;

}
