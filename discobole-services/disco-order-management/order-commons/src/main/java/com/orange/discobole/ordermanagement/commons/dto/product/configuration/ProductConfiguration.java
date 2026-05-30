// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductConfiguration {
    private String id;
    private Boolean isSelectable;
    private Boolean isSelected;
    private Boolean isBundle;
    private Boolean isVisible;
    private Boolean isInstallable;
    private Integer quantity;
    private String version;
    private ProductRefOrValue product;
    private BundledProductOfferingOption bundledProductOfferingOption;
    private BundledGroupProductOffering bundledGroupProductOffering;
    private ProductOfferingRef productOffering;
    @JsonProperty("configurationPrice")
    private List<ConfigurationPrice> configurationPrices;
    private ProductSpecificationRef productSpecification;
    @JsonProperty("configurationCharacteristic")
    private List<ConfigurationCharacteristic> configurationCharacteristics;
    @JsonProperty("policy")
    private List<PolicyRef> policies;
    @JsonProperty("configurationAction")
    private List<ConfigurationAction> configurationActions;
    @JsonProperty("configurationTerm")
    private List<ConfigurationTerm> configurationTerms;
    @JsonProperty("productConfiguration")
    private List<ProductConfiguration> productConfigurations;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
}