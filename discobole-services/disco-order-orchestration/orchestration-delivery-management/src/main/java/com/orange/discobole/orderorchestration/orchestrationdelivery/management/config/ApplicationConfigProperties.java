// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.config;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "config")
@Validated
@Getter
@Setter
public class ApplicationConfigProperties {
    @NotBlank(message = "The service catalog management Url must not be null or empty")
    private String serviceCatalogManagementUrl;

    @NotBlank(message = "The serviceOrdering Url must not be null or empty")
    private String serviceOrderingUrl;

    @NotBlank(message = "The shippingOrder Url must not be null or empty")
    private String shippingOrderUrl;
}
