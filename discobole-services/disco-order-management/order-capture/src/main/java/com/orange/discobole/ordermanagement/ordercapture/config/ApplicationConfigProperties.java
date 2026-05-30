// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.config;

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
    @NotBlank(message = "The product Offering Url must not be null or empty")
    private String productOfferingUrl;

    @NotBlank(message = "The product Offering qualification Url must not be null or empty")
    private String productOfferingQualificationUrl;

    @NotBlank(message = "The product configuration Url must not be empty")
    private String productConfigurationUrl;

    @NotBlank(message = "The product configuration mocked Url must not be empty")
    private String productConfigurationMockedUrl;

    @NotBlank(message = "The product specification Url must not be null or empty")
    private String productSpecificationUrl;

    @NotBlank(message = "The product offering price Url must not be null or empty")
    private String productOfferingPriceUrl;
    @NotBlank(message = "The inventory management Url must not be null or empty")
    private String inventoryManagementUrl;

    @NotBlank(message = "The product inventory Url must not be null or empty")
    private String productInventoryUrl;

    @NotBlank(message = "The order inventory Url must not be empty")
    private String orderInventoryUrl;

    @NotBlank(message = "The party management Url must not be null or empty")
    private String partyRoleManagementUrl;

    @NotBlank(message = "The product stock management Url must not be null or empty")
    private String productStockManagementUrl;
    @NotBlank(message = "The payment management Url must not be null or empty")
    private String paymentManagementUrl;

    @NotBlank(message = "The account management Url must not be null or empty")
    private String accountManagementUrl;
    @NotBlank(message = "The eligibility management Url must not be null or empty")
    private String serviceQualificationManagementUrl;

    @NotBlank(message = "The appointment management Url must not be null or empty")
    private String appointmentManagementUrl;

    @NotBlank(message = "The party management Url must not be null or empty")
    private String partyManagementUrl;
}