// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.generated.fallout.FalloutIncidentStateChangeEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.BaseEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class KafkaTyper {

    private static final List<String> SUB_PACKAGES = List.of("poi", "som", "cood", "shom");

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
            JavaType javaType = typeFactory.constructType(eventClass);
            return javaType;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CoodNonRecoverableAndNonRetryableException(new CoodTechnicalException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, "Error deserializing Kafka Json body."));
        }
    }

    /**
     * Finds the Class object for the specified class name in the base package.
     *
     * @param className The name of the class to find.
     * @return The Class object for the specified class name, or null if not found.
     */
    private static Class<?> findClassInPackage(String className) throws ClassNotFoundException {
        if (FalloutIncidentStateChangeEvent.class.getSimpleName().equals(className)) {
            return Class.forName(FalloutIncidentStateChangeEvent.class.getName());
        }
        if (ProductOrderStateChangeEvent.class.getSimpleName().equals(className)) {
            return Class.forName(ProductOrderStateChangeEvent.class.getName());
        }
        for (int i = 0; i < SUB_PACKAGES.size(); i++) {
            try {
                return BaseEvent.class.getClassLoader().loadClass(BaseEvent.class.getPackage().getName() + "." + SUB_PACKAGES.get(i) + "." + className);
            } catch (ClassNotFoundException e) {
                log.trace("Class {} not found in {} {}.", className, BaseEvent.class.getPackage().getName(), SUB_PACKAGES);
            }
        }

        throw new CoodTechnicalException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, "Class " + className + " not found while parse event.");
    }
}
