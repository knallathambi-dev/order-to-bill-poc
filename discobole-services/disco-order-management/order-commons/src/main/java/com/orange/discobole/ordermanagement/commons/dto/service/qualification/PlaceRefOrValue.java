// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.service.qualification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
        property = "@type",
        visible = true
)
@JsonSubTypes({@JsonSubTypes.Type(
        value = PlaceRef.class,
        name = "PlaceRef"
), @JsonSubTypes.Type(
        value = GeographicSite.class,
        name = "GeographicSite"
)})
public class PlaceRefOrValue {
    @JsonProperty("@type")
    private String type;
}