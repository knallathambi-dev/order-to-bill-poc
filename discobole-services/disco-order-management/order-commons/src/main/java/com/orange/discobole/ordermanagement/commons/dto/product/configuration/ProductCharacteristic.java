// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.configuration;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(
        value = {"@type"},
        allowSetters = true
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type",
        visible = true,
        defaultImpl = ProductStringCharacteristic.class
)
@JsonSubTypes({@JsonSubTypes.Type(
        value = ProductObjectCharacteristic.class,
        name = "ObjectCharacteristic"
), @JsonSubTypes.Type(
        value = ProductStringCharacteristic.class,
        name = "StringCharacteristic"
), @JsonSubTypes.Type(
        value = ProductValidityCharacteristic.class,
        name = "ValidityCharacteristic"
), @JsonSubTypes.Type(
        value = ProductDateCharacteristic.class,
        name = "DateCharacteristic"
), @JsonSubTypes.Type(
        value = ProductAddressCharacteristic.class,
        name = "AddressCharacteristic"
)})
public class ProductCharacteristic {
    private String id;
    private String name;
    private CharacteristicRelationship characteristicRelationship;
    private String valueType;
    private String unitOfMeasure;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
}