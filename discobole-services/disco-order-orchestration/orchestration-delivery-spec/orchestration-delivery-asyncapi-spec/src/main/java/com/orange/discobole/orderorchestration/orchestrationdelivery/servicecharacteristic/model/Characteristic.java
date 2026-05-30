// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
package com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model;

import com.fasterxml.jackson.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(
        name = "Characteristic",
        description = "Describes a given characteristic of the product through a name/value pair"
)
@JsonIgnoreProperties(
        value = {"@type"},
        allowSetters = true
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type",
        visible = true
)
@JsonSubTypes({@JsonSubTypes.Type(
        value = AddressCharacteristic.class,
        name = "AddressCharacteristic"
), @JsonSubTypes.Type(
        value = BooleanArrayCharacteristic.class,
        name = "BooleanArrayCharacteristic"
), @JsonSubTypes.Type(
        value = BooleanCharacteristic.class,
        name = "BooleanCharacteristic"
), @JsonSubTypes.Type(
        value = FloatArrayCharacteristic.class,
        name = "FloatArrayCharacteristic"
), @JsonSubTypes.Type(
        value = FloatCharacteristic.class,
        name = "FloatCharacteristic"
), @JsonSubTypes.Type(
        value = IntegerArrayCharacteristic.class,
        name = "IntegerArrayCharacteristic"
), @JsonSubTypes.Type(
        value = IntegerCharacteristic.class,
        name = "IntegerCharacteristic"
), @JsonSubTypes.Type(
        value = ObjectArrayCharacteristic.class,
        name = "ObjectArrayCharacteristic"
), @JsonSubTypes.Type(
        value = ObjectCharacteristic.class,
        name = "ObjectCharacteristic"
), @JsonSubTypes.Type(
        value = StringArrayCharacteristic.class,
        name = "StringArrayCharacteristic"
), @JsonSubTypes.Type(
        value = StringCharacteristic.class,
        name = "StringCharacteristic"
), @JsonSubTypes.Type(
        value = ValidityCharacteristic.class,
        name = "ValidityCharacteristic"
), @JsonSubTypes.Type(
        value = DateCharacteristic.class,
        name = "DateCharacteristic"
)})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Characteristic {
    private String id;
    private String name;
    private String valueType;

    @JsonProperty("@baseType")
    private String baseType;

    @JsonProperty("@type")
    private String atType;
}
