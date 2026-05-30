// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.featuresToDisable(
                DeserializationFeature.ACCEPT_FLOAT_AS_INT
        );
    }

    /**
     * Support for Java date and time API.
     *
     * @return the corresponding Jackson module.
     */
    @Bean
    public JavaTimeModule javaTimeModule() {
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Instant.class, new RemoveMillisecondInstantSerializer());
        javaTimeModule.addSerializer(OffsetDateTime.class, new RemoveMillisecondOffsetDateTimeSerializer());
        return javaTimeModule;
    }

    @Bean
    public Jdk8Module jdk8TimeModule() {
        return new Jdk8Module();
    }

    public static class RemoveMillisecondInstantSerializer extends JsonSerializer<Instant> {

        private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                .withZone(ZoneId.of("UTC"));

        @Override
        public void serialize(final Instant instant, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
            final String serializedInstant = dateTimeFormatter.format(instant);
            jsonGenerator.writeString(serializedInstant);
        }
    }
    public static class RemoveMillisecondOffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {
        private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                .withZone(ZoneId.of("UTC"));

        @Override
        public void serialize(final OffsetDateTime offsetDateTime, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
            final String serializedInstant = dateTimeFormatter.format(offsetDateTime);
            jsonGenerator.writeString(serializedInstant);
        }
    }
}
