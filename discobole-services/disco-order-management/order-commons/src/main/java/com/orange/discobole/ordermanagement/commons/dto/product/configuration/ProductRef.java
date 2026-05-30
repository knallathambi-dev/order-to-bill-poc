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
import jakarta.validation.constraints.NotEmpty;
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
public class ProductRef extends ProductRefOrValue {

    @NotEmpty(message = "ProductRef.id cannot be empty/null")
    @JsonProperty(value = "id", required = true)
    private String id = null;
    @JsonProperty("name")
    private String name = null;
    @JsonProperty("href")
    private String href = null;
    @JsonProperty("@baseType")
    private String baseType = null;

    @JsonProperty("@schemaLocation")
    private String schemaLocation = null;

    @JsonProperty("@type")
    private String type = "PartyRef";

    @JsonProperty("@referredType")
    private String referredType = null;

}
