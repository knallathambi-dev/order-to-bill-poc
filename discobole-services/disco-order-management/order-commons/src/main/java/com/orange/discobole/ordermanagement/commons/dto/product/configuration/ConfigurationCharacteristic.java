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
public class ConfigurationCharacteristic {
    private String description;
    private String id;
    private Boolean isConfigurable;
    private Boolean configurable;
    private Boolean isVisible;
    private Integer maxCardinality;
    private Integer minCardinality;
    private String name;
    private String regex;
    private String valueType;
    private List<ConfigurationCharacteristicRelationship> configurationCharacteristicRelationships;
    private List<ConfigurationCharacteristicValue> configurationCharacteristicValues;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("configurationPrice")
    private List<ConfigurationPrice> configurationPrices;
}