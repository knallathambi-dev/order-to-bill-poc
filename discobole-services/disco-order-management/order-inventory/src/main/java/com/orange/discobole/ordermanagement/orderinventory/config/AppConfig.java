// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Getter
@Configuration
public class AppConfig {

    @Value("${app.name}")
    private String name;

    @Value("${app.isDeprecated}")
    private Boolean isDeprecated;

    @Value("${app.version}")
    private String applicationVersion;

    @Value("${app.designVersion}")
    private String designVersion;

    @Value("${app.tmfVersion}")
    private String tmfVersion;

    @Value("${app.cache.caffeine.spec.maximumSize}")
    private int maximumSize;

    @Value("${app.cache.caffeine.spec.expireAfterWrite}")
    private Duration expireAfterWrite;

    @Value("${app.defaultPageSize}")
    private int defaultPageSize;
}