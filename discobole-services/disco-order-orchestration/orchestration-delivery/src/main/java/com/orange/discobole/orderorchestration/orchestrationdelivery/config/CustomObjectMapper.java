// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
/**
 * CustomObjectMapper is a utility class for providing a configured ObjectMapper instance
 * with JavaTimeModule and specific configuration settings.
 */
public class CustomObjectMapper {

    // The shared instance of ObjectMapper
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Configure the ObjectMapper with JavaTimeModule and specific settings
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Retrieves a configured ObjectMapper instance.
     *
     * @return A configured ObjectMapper instance.
     */
    public static ObjectMapper get() {
        // Return a copy of the objectMapper to avoid exposing internal representation
        return objectMapper.copy();
    }
}
