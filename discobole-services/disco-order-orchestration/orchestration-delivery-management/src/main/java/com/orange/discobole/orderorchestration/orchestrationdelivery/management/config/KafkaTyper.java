// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.config;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.orange.discobole.orderorchestration.exception.DiscoException;
import com.orange.discobole.orderorchestration.config.custom.CustomObjectMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.BaseEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class KafkaTyper {

    private static final List<String> SUB_PACKAGES = List.of(
            BaseEvent.class.getPackage().getName() + ".poi",
            BaseEvent.class.getPackage().getName() + ".som",
            BaseEvent.class.getPackage().getName() + ".cood",
            "com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom"
    );

    public static JavaType typer(byte[] data) {

        ObjectMapper objectMapper = CustomObjectMapper.get();
        try {
            // Deserialize JSON payload into JsonNode
            JsonNode jsonNode = objectMapper.readTree(data);

            // Extract the "@type" property to determine the actual class to instantiate
            String eventType = jsonNode.path("eventType").asText();

            // Search for the class in the specified base package
            Class<?> eventClass = findClassInPackage(eventType);
            TypeFactory typeFactory = TypeFactory.defaultInstance();

            // Create JavaType from Class
            return typeFactory.constructType(eventClass);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON", e);
        }
    }

    /**
     * Finds the Class object for the specified class name in the base package.
     *
     * @param className The name of the class to find.
     * @return The Class object for the specified class name, or null if not found.
     */
    private static Class<?> findClassInPackage(String className) {
        for (int i = 0; i < SUB_PACKAGES.size(); i++) {
            try {
                return Class.forName(SUB_PACKAGES.get(i) + "." + className);
            } catch (Exception e) {
                log.info("Class {} not found in {} {} ,, message: {}.", className, BaseEvent.class.getPackage().getName(), SUB_PACKAGES.get(i), e.getMessage());
            }
        }
        throw new DiscoException("Class " + className + " not found while parse event.");
    }
}
