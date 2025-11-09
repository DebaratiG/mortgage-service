package com.mortgage.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuration class for customizing Jackson JSON serialization and deserialization.
 */
@Configuration
public class JacksonConfig {

    /**
     * Configures the ObjectMapper with custom serialization settings.
     * <p>
     * Features configured:
     * <ul>
     *   <li>BigDecimal serialization as plain numbers (not scientific notation)</li>
     *   <li>Date/time serialization as ISO-8601 strings</li>
     *   <li>Java Time API support for Instant and other temporal types</li>
     * </ul>
     *
     * @param builder Jackson2ObjectMapperBuilder for creating ObjectMapper
     * @return Configured ObjectMapper instance
     *
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        var objectMapper = builder.build();

        // Configure BigDecimal serialization
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);

        // Configure date/time serialization
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Register Java Time module for proper temporal type handling
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}
