// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.orderinventory.IntegrationTest;
import com.orange.discobole.ordermanagement.orderinventory.config.AppConfig;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Status;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link StatusControllerImpl}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@IntegrationTest
@AutoConfigureMockMvc
class StatusControllerImplTest {
    private static final String STATUS_API_URL = "/status";
    private static final String DEFAULT_SERVICE_NAME = "Test Service";
    private static final String DEFAULT_VERSION = "1.0.0";
    private static final String DEFAULT_DESIGN_VERSION = "1.0.1";
    private static final String DEFAULT_TMF_VERSION = "4.0.0";
    private static final String ALTERNATE_VERSION = "2.0.0";
    private static final String ALTERNATE_TMF_VERSION = "5.0.0";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc restMockMvc;

    @MockBean
    private AppConfig appConfig;

    @BeforeEach
    void setUpDefaultMocks() {
        when(appConfig.getName()).thenReturn(DEFAULT_SERVICE_NAME);
        when(appConfig.getApplicationVersion()).thenReturn(DEFAULT_VERSION);
        when(appConfig.getDesignVersion()).thenReturn(DEFAULT_DESIGN_VERSION);
        when(appConfig.getTmfVersion()).thenReturn(DEFAULT_TMF_VERSION);
    }

    @Test
    @DisplayName("Given the service is running, " +
            "when fetching service status, " +
            "then the status is returned successfully with all fields")
    void shouldReturnCompleteServiceStatus() throws Exception {
        // Given & When
        String jsonResponse = restMockMvc.perform(get(STATUS_API_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(DEFAULT_SERVICE_NAME))
                .andExpect(jsonPath("$.status").value(StatusEnum.OK.name().toLowerCase()))
                .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
                .andExpect(jsonPath("$.designVersion").value(DEFAULT_DESIGN_VERSION))
                .andExpect(jsonPath("$.tmfVersion").value(DEFAULT_TMF_VERSION))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        validateResponse(jsonResponse, DEFAULT_SERVICE_NAME, StatusEnum.OK, DEFAULT_VERSION, DEFAULT_DESIGN_VERSION, DEFAULT_TMF_VERSION);
    }

    @Test
    @DisplayName("Given the service configuration is incomplete, " +
            "when fetching service status, " +
            "then the status is returned with missing optional fields")
    void shouldHandleIncompleteServiceConfiguration() throws Exception {
        // Given
        when(appConfig.getName()).thenReturn(null);
        when(appConfig.getApplicationVersion()).thenReturn(ALTERNATE_VERSION);
        when(appConfig.getDesignVersion()).thenReturn(null);
        when(appConfig.getTmfVersion()).thenReturn(ALTERNATE_TMF_VERSION);

        // When
        String jsonResponse = restMockMvc.perform(get(STATUS_API_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.status").value(StatusEnum.OK.name().toLowerCase()))
                .andExpect(jsonPath("$.version").value(ALTERNATE_VERSION))
                .andExpect(jsonPath("$.designVersion").doesNotExist())
                .andExpect(jsonPath("$.tmfVersion").value(ALTERNATE_TMF_VERSION))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        validateResponse(jsonResponse, null, StatusEnum.OK, ALTERNATE_VERSION, null, ALTERNATE_TMF_VERSION);
    }

    private void validateResponse(String jsonResponse, String expectedName, StatusEnum expectedStatus,
                                  String expectedVersion, String expectedDesignVersion, String expectedTmfVersion) throws Exception {
        Status responseStatus = objectMapper.readValue(jsonResponse, Status.class);

        assertThat(responseStatus).isNotNull();
        assertThat(responseStatus.getName()).isEqualTo(expectedName);
        assertThat(responseStatus.getStatus()).isEqualTo(expectedStatus);
        assertThat(responseStatus.getVersion()).isEqualTo(expectedVersion);
        assertThat(responseStatus.getDesignVersion()).isEqualTo(expectedDesignVersion);
        assertThat(responseStatus.getTmfVersion()).isEqualTo(expectedTmfVersion);
    }
}