// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller.impl;

import com.orange.discobole.productinventory.api.v1.VersionApi;
import com.orange.discobole.productinventory.config.AppConfig;
import com.orange.discobole.productinventory.dto.v1.ServiceVersion;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "Version",
        description = "the Version API"
)
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@RequestMapping("")
public class VersionApiImpl implements VersionApi {
    private final AppConfig appConfig;

    @Override
    public ResponseEntity<ServiceVersion> getServiceVersion() {
        return ResponseEntity.ok(new ServiceVersion(appConfig.getApplicationVersion()));
    }
}
