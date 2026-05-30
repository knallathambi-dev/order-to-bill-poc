// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Objects;

/**
 * ProductOffering reference. A product offering represents entities that are orderable from the provider of the catalog, this resource includes pricing information.
 **/
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class ProductOfferingRefEntity {
    @Indexed
    private String id;
    private String href;
    private String name;
    private String version;
    @Indexed
    @JsonProperty("@type")
    @JsonAlias("atType")
    private String atType;
    @JsonProperty("@baseType")
    private String atBaseType;
    @JsonProperty("@schemaLocation")
    @JsonAlias("atSchemaLocation")
    private String atSchemaLocation;
    @JsonProperty("@referredType")
    @JsonAlias("atReferredType")
    private String atReferredType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductOfferingRefEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


