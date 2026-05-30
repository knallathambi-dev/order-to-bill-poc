// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "config")
@Validated
@Getter
@Setter
public class ApplicationConfigProperties {

    @NotBlank(message = "The productSpecById Url must not be null or empty")
    private String productSpecByIdCatalogUrl;

    @NotBlank(message = "The product management Url must not be null or empty")
    private String productManagementUrl;

    @NotBlank(message = "The service catalog management Url must not be null or empty")
    private String serviceCatalogManagementUrl;

    @NotBlank(message = "The productOrderByIdUrl Url must not be null or empty")
    private String productOrderByIdUrl;

    @NotBlank(message = "The serviceOrdering Url must not be null or empty")
    private String serviceOrderingUrl;

    @NotBlank(message = "The fallout Url must not be null or empty")
    private String falloutUrl;
}
