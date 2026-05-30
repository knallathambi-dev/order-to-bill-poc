// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification;

import com.fasterxml.jackson.annotation.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ValidFor;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductSpecificationRelationshipType;
import jakarta.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "href",
        "description",
        "relationshipType",
        "validFor",
        "name",
        "@type",
        "@baseType",
        "@schemaLocation"
})
@Generated("jsonschema2pojo")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecificationRelationship {

    @JsonProperty("id")
    private String id;
    @JsonProperty("href")
    private String href;
    @JsonProperty("description")
    private String description;
    @JsonProperty("relationshipType")
    private ProductSpecificationRelationshipType relationshipType;
    @JsonProperty("validFor")
    private ValidFor validFor;
    @JsonProperty("name")
    private String name;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("href")
    public String getHref() {
        return href;
    }

    @JsonProperty("href")
    public void setHref(String href) {
        this.href = href;
    }
    @JsonProperty("name")
    public String getName() {return name;}
    @JsonProperty("name")
    public void setName(String name) {this.name = name;}
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("relationshipType")
    public ProductSpecificationRelationshipType getRelationshipType() {
        return relationshipType;
    }

    @JsonProperty("relationshipType")
    public void setRelationshipType(ProductSpecificationRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    @JsonProperty("validFor")
    public ValidFor getValidFor() {
        return validFor;
    }

    @JsonProperty("validFor")
    public void setValidFor(ValidFor validFor) {
        this.validFor = validFor;
    }

    @JsonProperty("@type")
    public String getType() {
        return type;
    }

    @JsonProperty("@type")
    public void setType(String type) {
        this.type = type;
    }

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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
