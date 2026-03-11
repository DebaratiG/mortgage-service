package com.mortgage.repository;

import com.mortgage.entity.MortgageRateEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * Repository interface for managing mortgage rate entities.
 * <p>
 * This repository interacts with the `mortgage_rate` table in the database.
 *
 */
@Repository
public interface MortgageRepository extends JpaRepository<MortgageRateEntity, Long> {

    List<MortgageRateEntity> findAll(Sort sort);
    Optional<MortgageRateEntity> findByMaturityPeriod(Integer maturityPeriod);
}
