// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.controller;

import com.orange.discobole.ordermanagement.orderinventory.api.v1.VersionApi;
import com.orange.discobole.ordermanagement.orderinventory.config.AppConfig;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Version;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionControllerImpl implements VersionApi {

    private final AppConfig appConfig;
    private final Logger log = LoggerFactory.getLogger(VersionControllerImpl.class);

    public VersionControllerImpl(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public ResponseEntity<Version> getServiceVersion() {
        log.info("Fetching service version");

        String fullVersion = appConfig.getApplicationVersion();
        Boolean isDeprecated = appConfig.getIsDeprecated();

        VersionInfo parsedVersionInfo = parseVersionString(fullVersion);
        Version serviceVersion = buildVersionResponse(parsedVersionInfo, isDeprecated);

        log.info("Service version fetched successfully: {}", serviceVersion);
        return ResponseEntity.ok(serviceVersion);
    }

    private VersionInfo parseVersionString(String versionString) {
        if (versionString == null || versionString.isEmpty()) {
            return null;
        }

        String[] versionParts = versionString.split("\\.");
        String majorVersion = versionParts.length > 0 ? versionParts[0] : null;
        String minorVersion = versionParts.length > 1 ? versionParts[1] : null;
        String patchVersion = versionParts.length > 2 ? versionParts[2] : null;

        if (patchVersion != null && patchVersion.contains("-SNAPSHOT")) {
            patchVersion = patchVersion.replace("-SNAPSHOT", "");
        }

        if (majorVersion == null && minorVersion == null && patchVersion == null) {
            return null;
        }

        return VersionInfo.builder()
                .major(majorVersion)
                .minor(minorVersion)
                .patch(patchVersion)
                .build();
    }

    private Version buildVersionResponse(VersionInfo versionInfo, boolean isDeprecated) {
        return Version.builder()
                .version(versionInfo)
                .deprecated(isDeprecated)
                .build();
    }
}