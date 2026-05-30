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
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Version;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.VersionInfo;
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
 * Integration tests for {@link VersionControllerImpl}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@IntegrationTest
@AutoConfigureMockMvc
class VersionControllerImplTest {
    private static final String VERSION_API_URL = "/version";
    private static final String VALID_VERSION = "1.2.3";
    private static final String SNAPSHOT_VERSION = "2.0.0-SNAPSHOT";
    private static final String PARTIAL_VERSION = "1.2";

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        when(appConfig.getApplicationVersion()).thenReturn(VALID_VERSION);
        when(appConfig.getIsDeprecated()).thenReturn(false);
    }

    @Test
    @DisplayName("Given a valid version string, " +
            "when fetching service version, " +
            "then the correct version response is returned")
    void shouldReturnCorrectVersionForValidVersion() throws Exception {
        // Given & When
        String jsonResponse = restMockMvc.perform(get(VERSION_API_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version.major").value("1"))
                .andExpect(jsonPath("$.version.minor").value("2"))
                .andExpect(jsonPath("$.version.patch").value("3"))
                .andExpect(jsonPath("$.deprecated").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        validateResponse(jsonResponse, "1", "2", "3", false);
    }

    @Test
    @DisplayName("Given a version string with SNAPSHOT, " +
            "when fetching service version, " +
            "then SNAPSHOT is removed from the patch field")
    void shouldHandleSnapshotVersion() throws Exception {
        // Given
        when(appConfig.getApplicationVersion()).thenReturn(SNAPSHOT_VERSION);
        when(appConfig.getIsDeprecated()).thenReturn(true);

        // When
        String jsonResponse = restMockMvc.perform(get(VERSION_API_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version.major").value("2"))
                .andExpect(jsonPath("$.version.minor").value("0"))
                .andExpect(jsonPath("$.version.patch").value("0"))
                .andExpect(jsonPath("$.deprecated").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        validateResponse(jsonResponse, "2", "0", "0", true);
    }

    @Test
    @DisplayName("Given a partial version string, " +
            "when fetching service version, " +
            "then missing fields are excluded")
    void shouldHandlePartialVersionString() throws Exception {
        // Given
        when(appConfig.getApplicationVersion()).thenReturn(PARTIAL_VERSION);

        // When
        String jsonResponse = restMockMvc.perform(get(VERSION_API_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version.major").value("1"))
                .andExpect(jsonPath("$.version.minor").value("2"))
                .andExpect(jsonPath("$.version.patch").doesNotExist())
                .andExpect(jsonPath("$.deprecated").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        validateResponse(jsonResponse, "1", "2", null, false);
    }

    @Test
    @DisplayName("Given an empty version string, " +
            "when fetching service version, " +
            "then the version field is excluded")
    void shouldExcludeVersionFieldForEmptyVersionString() throws Exception {
        // Given
        when(appConfig.getApplicationVersion()).thenReturn("");

        // When & Then
        restMockMvc.perform(get(VERSION_API_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").doesNotExist())
                .andExpect(jsonPath("$.deprecated").value(false));
    }

    @Test
    @DisplayName("Given a null version string, " +
            "when fetching service version, " +
            "then the version field is excluded")
    void shouldExcludeVersionFieldForNullVersionString() throws Exception {
        // Given
        when(appConfig.getApplicationVersion()).thenReturn(null);

        // When & Then
        restMockMvc.perform(get(VERSION_API_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").doesNotExist()) // Version field excluded
                .andExpect(jsonPath("$.deprecated").value(false));
    }

    private void validateResponse(String jsonResponse, String expectedMajor, String expectedMinor,
                                  String expectedPatch, boolean expectedDeprecated) throws Exception {
        Version responseVersion = objectMapper.readValue(jsonResponse, Version.class);

        assertThat(responseVersion).isNotNull();
        assertThat(responseVersion.getDeprecated()).isEqualTo(expectedDeprecated);

        VersionInfo versionInfo = responseVersion.getVersion();
        if (expectedMajor == null && expectedMinor == null && expectedPatch == null) {
            assertThat(versionInfo).isNull();
        } else {
            assertThat(versionInfo).isNotNull();
            assertThat(versionInfo.getMajor()).isEqualTo(expectedMajor);
            assertThat(versionInfo.getMinor()).isEqualTo(expectedMinor);
            assertThat(versionInfo.getPatch()).isEqualTo(expectedPatch);
        }
    }
}