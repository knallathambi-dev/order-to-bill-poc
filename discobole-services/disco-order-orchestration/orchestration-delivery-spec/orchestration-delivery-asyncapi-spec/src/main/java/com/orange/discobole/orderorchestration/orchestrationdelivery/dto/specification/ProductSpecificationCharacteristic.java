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
import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "configurable",
        "description",
        "extensible",
        "isUnique",
        "maxCardinality",
        "minCardinality",
        "name",
        "regex",
        "valueType",
        "productSpecCharRelationship",
        "productSpecCharacteristicValue",
        "validFor",
        "@baseType",
        "@schemaLocation",
        "@type"
})
@Generated("jsonschema2pojo")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductSpecificationCharacteristic {

    @JsonProperty("id")
    private String id;
    @JsonProperty("configurable")
    private Boolean configurable;
    @JsonProperty("description")
    private String description;
    @JsonProperty("extensible")
    private String extensible;
    @JsonProperty("isUnique")
    private String isUnique;
    @JsonProperty("maxCardinality")
    private String maxCardinality;
    @JsonProperty("minCardinality")
    private String minCardinality;
    @JsonProperty("name")
    private String name;
    @JsonProperty("regex")
    private String regex;
    @JsonProperty("valueType")
    private String valueType;
    @JsonProperty("productSpecCharRelationship")
    private List<ProductSpecificationCharacteristicRelationship> productSpecificationCharacteristicRelationship;
    @JsonProperty("productSpecCharacteristicValue")
    private List<ProductSpecCharacteristicValue> productSpecCharacteristicValue;
    @JsonProperty("validFor")
    private ValidFor validFor;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
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

    @JsonProperty("configurable")
    public Boolean getConfigurable() {
        return configurable;
    }

    @JsonProperty("configurable")
    public void setConfigurable(Boolean configurable) {
        this.configurable = configurable;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("extensible")
    public String getExtensible() {
        return extensible;
    }

    @JsonProperty("extensible")
    public void setExtensible(String extensible) {
        this.extensible = extensible;
    }

    @JsonProperty("isUnique")
    public String getIsUnique() {
        return isUnique;
    }

    @JsonProperty("isUnique")
    public void setIsUnique(String isUnique) {
        this.isUnique = isUnique;
    }

    @JsonProperty("maxCardinality")
    public String getMaxCardinality() {
        return maxCardinality;
    }

    @JsonProperty("maxCardinality")
    public void setMaxCardinality(String maxCardinality) {
        this.maxCardinality = maxCardinality;
    }

    @JsonProperty("minCardinality")
    public String getMinCardinality() {
        return minCardinality;
    }

    @JsonProperty("minCardinality")
    public void setMinCardinality(String minCardinality) {
        this.minCardinality = minCardinality;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("regex")
    public String getRegex() {
        return regex;
    }

    @JsonProperty("regex")
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @JsonProperty("valueType")
    public String getValueType() {
        return valueType;
    }

    @JsonProperty("valueType")
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    @JsonProperty("productSpecCharRelationship")
    public List<ProductSpecificationCharacteristicRelationship> getProductSpecificationCharacteristicRelationship() {
        return productSpecificationCharacteristicRelationship;
    }

    @JsonProperty("productSpecCharRelationship")
    public void setProductSpecificationCharacteristicRelationship(List<ProductSpecificationCharacteristicRelationship> productSpecificationCharacteristicRelationship) {
        this.productSpecificationCharacteristicRelationship = productSpecificationCharacteristicRelationship;
    }

    @JsonProperty("productSpecCharacteristicValue")
    public List<ProductSpecCharacteristicValue> getProductSpecCharacteristicValue() {
        return productSpecCharacteristicValue;
    }

    @JsonProperty("productSpecCharacteristicValue")
    public void setProductSpecCharacteristicValue(List<ProductSpecCharacteristicValue> productSpecCharacteristicValue) {
        this.productSpecCharacteristicValue = productSpecCharacteristicValue;
    }

    @JsonProperty("validFor")
    public ValidFor getValidFor() {
        return validFor;
    }

    @JsonProperty("validFor")
    public void setValidFor(ValidFor validFor) {
        this.validFor = validFor;
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

    @JsonProperty("@type")
    public String getType() {
        return type;
    }

    @JsonProperty("@type")
    public void setType(String type) {
        this.type = type;
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
