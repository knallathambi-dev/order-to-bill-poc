// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.controller;

import com.orange.discobole.ordermanagement.orderinventory.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link DocControllerImpl}.
 */
class DocControllerImplTest {

    @IntegrationTest
    @AutoConfigureMockMvc
    static class BaseTest {
        static final String DOC_API_URL = "/doc";

        @Autowired
        protected MockMvc restMockMvc;
    }

    @Nested
    class ValidYamlFileTest extends BaseTest {
        @DynamicPropertySource
        static void registerDynamicProperties(DynamicPropertyRegistry registry) {
            registry.add("api.docs.path", () -> "static/api-docs/order-inventory-spec.yaml");
        }

        @Test
        @DisplayName("Given a valid YAML file, " +
                "when fetching the documentation, " +
                "then JSON is returned successfully")
        void shouldReturnJsonForValidYamlFile() throws Exception {
            // Given & When & Then
            restMockMvc.perform(get(DOC_API_URL).accept("application/json"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(content().string("{\"openapi\":\"3.0.0\",\"info\":{\"title\":\"Test API\",\"version\":\"1.0.0-SNAPSHOT\"}}")); // Adjust this according to your actual YAML content
        }
    }

    @Nested
    class NotExistYamlFileTest extends BaseTest {
        @DynamicPropertySource
        static void registerDynamicProperties(DynamicPropertyRegistry registry) {
            registry.add("api.docs.path", () -> "static/api-docs/does-not-exist.yaml");
        }

        @Test
        @DisplayName("Given a not exist YAML file, " +
                "when fetching the documentation, " +
                "then a not found status is returned")
        void shouldReturn404_whenYamlFileIsMissing() throws Exception {
            restMockMvc.perform(get(DOC_API_URL))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class MalformedYamlFileTest extends BaseTest {
        @DynamicPropertySource
        static void registerDynamicProperties(DynamicPropertyRegistry registry) {
            registry.add("api.docs.path", () -> "static/api-docs/malformed-api-doc.yaml");
        }

        @Test
        @DisplayName("Given a not exist YAML file, " +
                "when fetching the documentation, " +
                "then a internal server status is returned")
        void shouldReturn500_whenYamlFileIsMalformed() throws Exception {
            restMockMvc.perform(get(DOC_API_URL))
                    .andExpect(status().isInternalServerError());
        }
    }
}