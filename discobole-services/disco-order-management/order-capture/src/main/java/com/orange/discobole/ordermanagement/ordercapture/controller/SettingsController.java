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
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.mapper.settings.SettingsMapper;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(OrderCaptureConstants.SETTING_URL)
@Slf4j
public class SettingsController {
    private final SettingsService settingsService;
    private final SettingsMapper settingsMapper;

    public SettingsController(SettingsService settingsService, SettingsMapper settingsMapper) {
        this.settingsService = settingsService;
        this.settingsMapper = settingsMapper;
    }

    @PostMapping
    public ResponseEntity<Settings> saveSettings(@RequestBody @Valid Settings settings) {
        log.debug("Received save settings request");
        SettingsEntity settingsEntity = settingsMapper.toEntity(settings);
        settingsEntity = settingsService.saveSettings(settingsEntity);
        Settings setting = settingsMapper.toDto(settingsEntity);
        return new ResponseEntity<>(setting, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Settings> getSettings() {
        log.debug("Received get settings request");
        SettingsEntity settingsEntity = settingsService.getSettings();
        Settings settings = settingsMapper.toDto(settingsEntity);
        return ResponseEntity.ok(settings);
    }
}