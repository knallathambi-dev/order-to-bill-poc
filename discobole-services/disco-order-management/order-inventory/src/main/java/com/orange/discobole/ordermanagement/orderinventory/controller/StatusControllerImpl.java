// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.controller;

import com.orange.discobole.ordermanagement.orderinventory.api.v1.StatusApi;
import com.orange.discobole.ordermanagement.orderinventory.config.AppConfig;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Status;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusControllerImpl implements StatusApi {
    private final AppConfig appConfig;
    private final Logger log = LoggerFactory.getLogger(StatusControllerImpl.class);

    public StatusControllerImpl(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public ResponseEntity<Status> getServiceHealth() {
        log.info("Fetching service status");

        Status status = Status.builder()
                .name(appConfig.getName())
                .status(StatusEnum.OK)
                .version(appConfig.getApplicationVersion())
                .designVersion(appConfig.getDesignVersion())
                .tmfVersion(appConfig.getTmfVersion())
                .build();

        log.info("Service status fetched: {}", status);
        return ResponseEntity.ok(status);
    }
}