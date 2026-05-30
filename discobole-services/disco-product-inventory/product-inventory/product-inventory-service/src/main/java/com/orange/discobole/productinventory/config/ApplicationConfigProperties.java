// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "config.path")
@Validated
@Getter
@Setter
public class ApplicationConfigProperties {
    @NotNull(message = "The product Offering Url must not be null")
    @NotBlank(message = "The product Offering Url must not be empty")
    private String productCatalogUrl;

    @NotNull(message = "The resource Inventory Management Url must not be null")
    @NotBlank(message = "The resource Inventory Management Url must not be empty")
    private String resourceInventoryManagementUrl;

    @Value("${config.enable.productCatalog}")
    private boolean enableProductCatalogCheck;

    @Value("${config.enable.resourceInventoryManagement}")
    private boolean enableResourceInventoryManagementCheck;

    @Value("${config.pagination.limit}")
    private Integer paginationLimit;
    @Value("${config.webclient.retry.max-attempts}")
    private Integer webClientMaxRetryAttempts;
    @Value("${config.webclient.retry.max-retry-delay}")
    private Long webClientMaxRetryDelay;
    @Value("${config.export.limit}")
    private int exportLimit;
}
