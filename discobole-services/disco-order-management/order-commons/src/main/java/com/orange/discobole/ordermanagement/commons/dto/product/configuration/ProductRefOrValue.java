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
import lombok.*;
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
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type",
        visible = true
)
@JsonSubTypes({@JsonSubTypes.Type(
        value = Product.class,
        name = "Product"
), @JsonSubTypes.Type(
        value = ProductRef.class,
        name = "ProductRef"
)})
public class ProductRefOrValue {
    @JsonProperty("@type")
    private String type;
}
