// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "@baseType",
        "@schemaLocation",
        "@type",
        "charSpecSeq",
        "href",
        "id",
        "name",
        "relationshipType",
        "validFor"
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecificationCharacteristicRelationship {

    @JsonProperty("@baseType")
    public String baseType;
    @JsonProperty("@schemaLocation")
    public String schemaLocation;
    @JsonProperty("@type")
    public String type;
    @JsonProperty("charSpecSeq")
    public Integer charSpecSeq;
    @JsonProperty("href")
    public String href;
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("relationshipType")
    public String relationshipType;
    @JsonProperty("validFor")
    public TimePeriod validFor;

    @JsonProperty("@baseType")
    public String getBaseType() {
        return baseType;
    }

    @JsonProperty("@baseType")
    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    @JsonProperty("@schemaLocation")
    public String getSchemaLocation() {
        return schemaLocation;
    }

    @JsonProperty("@schemaLocation")
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    @JsonProperty("@type")
    public String getType() {
        return type;
    }

    @JsonProperty("@type")
    public void setType(String type) {
        this.type = type;
    }
}
