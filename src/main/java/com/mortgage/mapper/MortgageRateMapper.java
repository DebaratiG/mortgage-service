package com.mortgage.mapper;

import com.mortgage.dto.MortgageRateDTO;
import com.mortgage.entity.MortgageRateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between MortgageRate and MortgageRateModel entities.
 * <p>
 * This interface provides methods to map data between the domain model
 * (`MortgageRateModel`) and the DTO (`MortgageRate`). It uses MapStruct
 * for automatic implementation of mapping logic.
 * <p>
 * Fields:
 *   INSTANCE: Singleton instance of the generated mapper implementation.
 * <p>
 * Methods:
 *      1. toModel: Converts a `MortgageRateModel` entity to a `MortgageRate` DTO.
 *      2. toDto: Converts a `MortgageRate` DTO to a `MortgageRateModel` entity.
 */
@Mapper(componentModel = "spring")
public interface MortgageRateMapper {

    MortgageRateMapper MORTGAGE_RATE_MAPPER = Mappers.getMapper(MortgageRateMapper.class);

    @Mapping(target = "lastUpdated", expression = "java(java.time.Instant.now())")
    MortgageRateDTO toDTO(MortgageRateEntity mortgageRateEntity);

    MortgageRateEntity toEntity(MortgageRateDTO mortgageRateDTO);

}
