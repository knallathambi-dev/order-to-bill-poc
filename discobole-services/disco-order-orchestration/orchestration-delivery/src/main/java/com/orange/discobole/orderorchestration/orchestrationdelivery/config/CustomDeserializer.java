// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.config;

import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * CustomDeserializer extends JsonDeserializer and customizes it to use a specific ObjectMapper configuration.
 *
 * @param <T> The type of the object to be deserialized.
 */
public class CustomDeserializer<T> extends ErrorHandlingDeserializer<T> {

    /**
     * Constructs a CustomDeserializer using a custom ObjectMapper configuration.
     */
    public CustomDeserializer() {
        // Calls the JsonDeserializer constructor with a custom ObjectMapper instance
        super(new JsonDeserializer<>(CustomObjectMapper.get()));
    }
}
