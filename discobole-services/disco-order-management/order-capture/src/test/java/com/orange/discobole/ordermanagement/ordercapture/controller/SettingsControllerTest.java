// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.controller;

import com.orange.discobole.ordermanagement.commons.dto.setting.Settings;
import com.orange.discobole.ordermanagement.ordercapture.IntegrationTest;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.repository.SettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.DEFAULT_ID;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.SETTING_URL;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SettingsControllerTest {

    @Autowired
    private SettingsRepository settingsRepository;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private MockMvc restSettingsMockMvc;

    @BeforeEach
    void initTest() {
        settingsRepository.deleteAll();
    }

    @DisplayName("Given a settings with null fields, " +
            "when save settings, " +
            "then return bad request due to validation error")
    @Test
    void shouldReturnBadRequestWhenSavingSettingsWithNullFields() throws Exception {
        // Given
        Settings settings = createSettings();
        settings.setReservePhysicalResourceEnabled(null);

        // When & Then
        restSettingsMockMvc.perform(post(SETTING_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(settings)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Given valid settings data, " +
            "when save settings, " +
            "then return the saved settings with correct values")
    @Test
    void shouldReturnCreatedSettingsWhenSavingSettings() throws Exception {
        // Given
        Settings settings = createSettings();

        // When & Then
        restSettingsMockMvc.perform(post(SETTING_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(settings)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservePhysicalResourceEnabled").value(TRUE))
                .andExpect(jsonPath("$.reserveLogicalResourceEnabled").value(FALSE))
                .andExpect(jsonPath("$.checkCommercialEligibilityEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkPaymentRefEnabled").value(FALSE))
                .andExpect(jsonPath("$.checkBillingAccountRefEnabled").value(FALSE))
                .andExpect(jsonPath("$.checkAndSetBillCycleDateEnabled").value(FALSE))
                .andExpect(jsonPath("$.checkPartyManagementEnabled").value(FALSE))
                .andExpect(jsonPath("$.checkTechnicalEligibilityEnabled").value(FALSE))
                .andExpect(jsonPath("$.checkAppointmentRefEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkFinancialEligibilityEnabled").value(TRUE));


    }

    @DisplayName("Given no settings in the database, " +
            "when get settings, " +
            "then return default settings with all features enabled")
    @Test
    void shouldReturnDefaultSettingsWhenNoSettingsInDB() throws Exception {
        // Given
        evictAllCaches();

        // When & Then
        restSettingsMockMvc
                .perform(get(SETTING_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservePhysicalResourceEnabled").value(TRUE))
                .andExpect(jsonPath("$.reserveLogicalResourceEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkCommercialEligibilityEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkPaymentRefEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkBillingAccountRefEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkAndSetBillCycleDateEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkPartyManagementEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkTechnicalEligibilityEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkAppointmentRefEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkFinancialEligibilityEnabled").value(FALSE));

    }

    @DisplayName("Given settings stored in the database, " +
            "when get settings, " +
            "then return the stored settings with correct values")
    @Test
    void shouldReturnSettingsWhenSettingsInDB() throws Exception {
        // Given
        SettingsEntity settingsEntity = createSettingsEntity();
        settingsEntity.setId(DEFAULT_ID);
        settingsRepository.save(settingsEntity);
        evictAllCaches();

        // When & Then
        restSettingsMockMvc
                .perform(get(SETTING_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservePhysicalResourceEnabled").value(TRUE))
                .andExpect(jsonPath("$.reserveLogicalResourceEnabled").value(FALSE))
                .andExpect(jsonPath("$.checkCommercialEligibilityEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkPaymentRefEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkBillingAccountRefEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkAndSetBillCycleDateEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkPartyManagementEnabled").value(FALSE))
                .andExpect(jsonPath("$.checkTechnicalEligibilityEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkAppointmentRefEnabled").value(TRUE))
                .andExpect(jsonPath("$.checkFinancialEligibilityEnabled").value(TRUE));


    }

    private SettingsEntity createSettingsEntity() {
        return SettingsEntity.builder()
                .reservePhysicalResourceEnabled(true)
                .reserveLogicalResourceEnabled(false)
                .checkCommercialEligibilityEnabled(true)
                .checkPaymentRefEnabled(true)
                .checkBillingAccountRefEnabled(true)
                .checkAndSetBillCycleDateEnabled(true)
                .checkPartyManagementEnabled(false)
                .checkTechnicalEligibilityEnabled(true)
                .checkAppointmentRefEnabled(true)
                .checkFinancialEligibilityEnabled(true)
                .build();
    }

    private Settings createSettings() {
        return Settings.builder()
                .reservePhysicalResourceEnabled(true)
                .reserveLogicalResourceEnabled(false)
                .checkCommercialEligibilityEnabled(true)
                .checkPaymentRefEnabled(false)
                .checkBillingAccountRefEnabled(false)
                .checkAndSetBillCycleDateEnabled(false)
                .checkPartyManagementEnabled(false)
                .checkTechnicalEligibilityEnabled(false)
                .checkAppointmentRefEnabled(true)
                .checkFinancialEligibilityEnabled(true)
                .build();
    }

    private void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }
}