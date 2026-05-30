// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.ordercapture.IntegrationTest;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.repository.SettingsRepository;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.util.Objects;

@IntegrationTest
class SettingsServiceImplTest {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        clearDatabase();
    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    @Test
    @DisplayName("Given no settings stored in the database, " +
            "when retrieving settings, " +
            "then the system should return default settings with all features enabled")
    void shouldReturnDefaultSettingsWhenNoSettingsInDB() {
        // Given
        clearCache();

        // When
        SettingsEntity settings = settingsService.getSettings();

        // Then
        Assertions.assertNotNull(settings);
        Assertions.assertTrue(settings.isReserveLogicalResourceEnabled());
        Assertions.assertTrue(settings.isReservePhysicalResourceEnabled());
        Assertions.assertTrue(settings.isCheckCommercialEligibilityEnabled());
        Assertions.assertTrue(settings.isCheckPaymentRefEnabled());
        Assertions.assertTrue(settings.isCheckBillingAccountRefEnabled());
        Assertions.assertTrue(settings.isCheckAndSetBillCycleDateEnabled());
        Assertions.assertTrue(settings.isCheckPartyManagementEnabled());
        Assertions.assertTrue(settings.isCheckTechnicalEligibilityEnabled());
        Assertions.assertTrue(settings.isCheckAppointmentRefEnabled());
        Assertions.assertFalse(settings.isCheckFinancialEligibilityEnabled());
    }

    @Test
    @DisplayName("Given existing settings saved in the database, " +
            "when retrieving settings, " +
            "then the system should return the stored settings accurately")
    void shouldReturnSettingsFromDBWhenSettingsExist() {
        // Given
        SettingsEntity settingsEntity = createSettings();
        settingsEntity.setId(OrderCaptureConstants.DEFAULT_ID);
        settingsRepository.save(settingsEntity);

        // When
        SettingsEntity retrievedSettings = settingsService.getSettings();

        // Then
        Assertions.assertNotNull(retrievedSettings);
        Assertions.assertTrue(retrievedSettings.isReservePhysicalResourceEnabled());
        Assertions.assertFalse(retrievedSettings.isReserveLogicalResourceEnabled());
        Assertions.assertTrue(retrievedSettings.isCheckCommercialEligibilityEnabled());
        Assertions.assertTrue(retrievedSettings.isCheckPaymentRefEnabled());
        Assertions.assertTrue(retrievedSettings.isCheckBillingAccountRefEnabled());
        Assertions.assertFalse(retrievedSettings.isCheckAndSetBillCycleDateEnabled());
        Assertions.assertTrue(retrievedSettings.isCheckPartyManagementEnabled());
        Assertions.assertFalse(retrievedSettings.isCheckTechnicalEligibilityEnabled());
        Assertions.assertTrue(retrievedSettings.isCheckAppointmentRefEnabled());
        Assertions.assertFalse(retrievedSettings.isCheckFinancialEligibilityEnabled());
    }

    @Test
    @DisplayName("Given a settings object to save, " +
            "when saving settings, " +
            "then the system should save and return the same settings object with correct values")
    void shouldReturnSavedSettingsWhenSavingSettings() {
        // Given
        SettingsEntity settingsEntity = createSettings();

        // When
        SettingsEntity savedSettings = settingsService.saveSettings(settingsEntity);

        // Then
        Assertions.assertNotNull(savedSettings);
        Assertions.assertTrue(savedSettings.isReservePhysicalResourceEnabled());
        Assertions.assertFalse(savedSettings.isReserveLogicalResourceEnabled());
        Assertions.assertTrue(savedSettings.isCheckCommercialEligibilityEnabled());
        Assertions.assertTrue(savedSettings.isCheckPaymentRefEnabled());
        Assertions.assertTrue(savedSettings.isCheckBillingAccountRefEnabled());
        Assertions.assertFalse(savedSettings.isCheckAndSetBillCycleDateEnabled());
        Assertions.assertTrue(savedSettings.isCheckPartyManagementEnabled());
        Assertions.assertFalse(savedSettings.isCheckTechnicalEligibilityEnabled());
        Assertions.assertTrue(savedSettings.isCheckAppointmentRefEnabled());
        Assertions.assertFalse(savedSettings.isCheckFinancialEligibilityEnabled());
    }

    private SettingsEntity createSettings() {
        return SettingsEntity.builder()
                .checkCommercialEligibilityEnabled(true)
                .reservePhysicalResourceEnabled(true)
                .reserveLogicalResourceEnabled(false)
                .checkPaymentRefEnabled(true)
                .checkBillingAccountRefEnabled(true)
                .checkAndSetBillCycleDateEnabled(false)
                .checkPartyManagementEnabled(true)
                .checkTechnicalEligibilityEnabled(false)
                .checkAppointmentRefEnabled(true)
                .checkFinancialEligibilityEnabled(false)
                .build();
    }

    private void clearCache() {
        cacheManager.getCacheNames()
                .forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    private void clearDatabase() {
        settingsRepository.deleteAll();
    }
}