// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.mapper.settings;

import com.orange.discobole.ordermanagement.commons.dto.setting.Settings;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface SettingsMapper {

    SettingsEntity toEntity(Settings settings);

    Settings toDto(SettingsEntity settingsEntity);
}