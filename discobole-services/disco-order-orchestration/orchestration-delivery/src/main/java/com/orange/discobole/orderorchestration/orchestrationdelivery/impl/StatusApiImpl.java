// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.api.v1.StatusApi;
import com.orange.discobole.orderorchestration.orchestrationdelivery.config.AppConfig;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ServiceStatus;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "Status",
        description = "Status API"
)
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class StatusApiImpl implements StatusApi {

    private final AppConfig appConfig;

    @Override
    public ResponseEntity<ServiceStatus> serviceStatusFind() {
        return ResponseEntity.ok(
                new ServiceStatus(
                        appConfig.getName(),
                        appConfig.getVersion(),
                        "Service is up and running"
                )
        );
    }
}
