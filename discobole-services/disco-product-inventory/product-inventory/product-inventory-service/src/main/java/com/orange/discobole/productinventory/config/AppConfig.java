// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Component
public class AppConfig {

    @Value("${spring.web.application.name}")
    private String applicationName;

    @Value("${spring.web.application.version}")
    private String applicationVersion;

    @Value("${terminateProductParameters.runs}")
    private int maxRuns;

    @Value("${terminateProductParameters.count}")
    private int maxCount;

    @Value("${config.tempPath}")
    private String tempPath;

    @Value("${config.purgeBatchSize}")
    private int purgeBatchSize;

    @Value("${config.cache.caffeine.spec.maximumSize}")
    private int maximumSize;

    @Value("${config.cache.caffeine.spec.expireAfterWrite}")
    private Duration expireAfterWrite;
}
