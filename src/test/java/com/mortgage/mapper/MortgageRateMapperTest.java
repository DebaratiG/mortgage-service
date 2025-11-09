package com.mortgage.mapper;

import com.mortgage.dto.MortgageRateDTO;
import com.mortgage.entity.MortgageRateEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class MortgageRateMapperTest {

    private final MortgageRateMapper mapper = Mappers.getMapper(MortgageRateMapper.class);

    @Test
    void testEntityToDto() {
        // GIVEN
        MortgageRateEntity entity = new MortgageRateEntity();
        entity.setId(1L);
        entity.setMaturityPeriod(120);
        entity.setInterestRate(BigDecimal.valueOf(5.25));
        entity.setLastUpdated(Instant.now());
        entity.setType("FIXED");


        // WHEN
        MortgageRateDTO dto = mapper.toDTO(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(entity.interestRate, dto.interestRate());
        assertEquals(entity.maturityPeriod, dto.maturityPeriod());
        assertEquals(entity.lastUpdated, dto.lastUpdated());
        assertEquals(entity.type, dto.type());
    }

    @Test
    void testDtoToEntity() {
        // GIVEN
        MortgageRateDTO dto = new MortgageRateDTO(120, BigDecimal.valueOf(3.5), Instant.now(), "FIXED");

        // WHEN
        MortgageRateEntity entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(dto.maturityPeriod(), entity.maturityPeriod);
        assertEquals(dto.interestRate(), entity.interestRate);
        assertEquals(dto.type(), entity.type);
        assertEquals(dto.lastUpdated(), entity.lastUpdated);
    }
}