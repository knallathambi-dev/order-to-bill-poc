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
import jakarta.annotation.Generated;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "validFor",
        "description",
        "isQualificationRequested",
        "@type",
        "@baseType",
        "@schemaLocation"
})
@Generated("jsonschema2pojo")
public class OperationSpecification {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("validFor")
    private ValidFor validFor;
    @JsonProperty("description")
    private String description;
    @JsonProperty("isQualificationRequested")
    private Boolean isQualificationRequested;
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

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("validFor")
    public ValidFor getValidFor() {
        return validFor;
    }

    @JsonProperty("validFor")
    public void setValidFor(ValidFor validFor) {
        this.validFor = validFor;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("isQualificationRequested")
    public Boolean getIsQualificationRequested() {
        return isQualificationRequested;
    }

    @JsonProperty("isQualificationRequested")
    public void setIsQualificationRequested(Boolean isQualificationRequested) {
        this.isQualificationRequested = isQualificationRequested;
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
