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
        defaultImpl = StringCharacteristic.class
)
@JsonSubTypes({@JsonSubTypes.Type(
        value = ObjectCharacteristic.class,
        name = "ObjectCharacteristic"
), @JsonSubTypes.Type(
        value = StringCharacteristic.class,
        name = "StringCharacteristic"
), @JsonSubTypes.Type(
        value = ValidityCharacteristic.class,
        name = "ValidityCharacteristic"
), @JsonSubTypes.Type(
        value = DateCharacteristic.class,
        name = "DateCharacteristic"
), @JsonSubTypes.Type(
        value = AddressCharacteristic.class,
        name = "AddressCharacteristic"
)})
public class CharacteristicValue {
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