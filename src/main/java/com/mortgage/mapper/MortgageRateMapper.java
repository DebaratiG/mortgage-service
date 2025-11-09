package com.mortgage.mapper;

import com.mortgage.dto.MortgageRateDTO;
import com.mortgage.entity.MortgageRateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * This interface provides methods to map data between the Entity
 * (`MortgageRateEntity`) and the DTO (`MortgageRateDTO`).
 * It uses MapStruct for automatic implementation of mapping logic.
 * Methods:
 *      1. toEntity: Converts a `MortgageRateDTO` to a `MortgageRateEntity`.
 *      2. toDto: Converts a `MortgageRateEntity` to a `MortgageRateEntity` entity.
 */
@Mapper(componentModel = "spring")
public interface MortgageRateMapper {

    @Mapping(target = "lastUpdated", expression = "java(java.time.Instant.now())")
    MortgageRateDTO toDTO(MortgageRateEntity mortgageRateEntity);

    @Mapping(target = "id", ignore = true)
    MortgageRateEntity toEntity(MortgageRateDTO mortgageRateDTO);

}
