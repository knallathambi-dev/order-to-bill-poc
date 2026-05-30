// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.config;

import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * CustomSerializer extends JsonSerializer and customizes it to use a specific ObjectMapper configuration.
 *
 * @param <T> The type of the object to be serialized.
 */
public class CustomSerializer<T> extends JsonSerializer<T> {

    /**
     * Constructs a CustomSerializer using a custom ObjectMapper configuration.
     */
    public CustomSerializer() {
        // Calls the JsonSerializer constructor with a custom ObjectMapper instance
        super(CustomObjectMapper.get());
    }
}
