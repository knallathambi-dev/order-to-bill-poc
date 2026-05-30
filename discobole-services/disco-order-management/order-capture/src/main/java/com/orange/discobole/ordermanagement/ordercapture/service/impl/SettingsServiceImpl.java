// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.repository.SettingsRepository;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.DEFAULT_ID;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.SETTING_CACHE;

@Component
@Slf4j
public class SettingsServiceImpl implements SettingsService {
    private final SettingsRepository settingsRepository;

    public SettingsServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    @Cacheable(SETTING_CACHE)
    public SettingsEntity getSettings() {
        SettingsEntity settingsEntity = settingsRepository.getSettingsById(DEFAULT_ID);
        if (settingsEntity == null) {
            settingsEntity = createDefaultSettings();
        }
        return settingsEntity;
    }

    @Override
    @CacheEvict(value = SETTING_CACHE, allEntries = true)
    public SettingsEntity saveSettings(SettingsEntity settingsEntity) {
        settingsEntity.setId(DEFAULT_ID);
        return settingsRepository.save(settingsEntity);
    }

    private SettingsEntity createDefaultSettings() {
        return SettingsEntity
                .builder()
                .reservePhysicalResourceEnabled(true)
                .reserveLogicalResourceEnabled(true)
                .checkCommercialEligibilityEnabled(true)
                .checkPaymentRefEnabled(true)
                .checkBillingAccountRefEnabled(true)
                .checkAndSetBillCycleDateEnabled(true)
                .checkPartyManagementEnabled(true)
                .checkTechnicalEligibilityEnabled(true)
                .checkAppointmentRefEnabled(true)
                .build();
    }
}