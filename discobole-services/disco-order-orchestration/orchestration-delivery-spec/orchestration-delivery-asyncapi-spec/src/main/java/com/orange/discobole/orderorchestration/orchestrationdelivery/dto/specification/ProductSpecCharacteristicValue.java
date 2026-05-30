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
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.TimeRange;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ValidFor;
import jakarta.annotation.Generated;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "isDefault",
        "rangeInterval",
        "regex",
        "unitOfMeasure",
        "valueFrom",
        "valueTo",
        "valueType",
        "validFor",
        "timeRange",
        "value",
        "@baseType",
        "@schemaLocation",
        "@type",
        "stockItem.stockItemCharacteristicValue.value"
})
@Generated("jsonschema2pojo")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductSpecCharacteristicValue {

    @JsonProperty("isDefault")
    private Boolean isDefault;
    @JsonProperty("rangeInterval")
    private String rangeInterval;
    @JsonProperty("regex")
    private String regex;
    @JsonProperty("unitOfMeasure")
    private String unitOfMeasure;
    @JsonProperty("valueFrom")
    private String valueFrom;
    @JsonProperty("valueTo")
    private String valueTo;
    @JsonProperty("valueType")
    private String valueType;
    @JsonProperty("validFor")
    private ValidFor validFor;
    @JsonProperty("timeRange")
    private TimeRange timeRange;
    @JsonProperty("value")
    private String value;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("stockItem.stockItemCharacteristicValue.value")
    private String stockItemStockItemCharacteristicValueValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("isDefault")
    public Boolean getIsDefault() {
        return isDefault;
    }

    @JsonProperty("isDefault")
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    @JsonProperty("rangeInterval")
    public String getRangeInterval() {
        return rangeInterval;
    }

    @JsonProperty("rangeInterval")
    public void setRangeInterval(String rangeInterval) {
        this.rangeInterval = rangeInterval;
    }

    @JsonProperty("regex")
    public String getRegex() {
        return regex;
    }

    @JsonProperty("regex")
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @JsonProperty("unitOfMeasure")
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    @JsonProperty("unitOfMeasure")
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    @JsonProperty("valueFrom")
    public String getValueFrom() {
        return valueFrom;
    }

    @JsonProperty("valueFrom")
    public void setValueFrom(String valueFrom) {
        this.valueFrom = valueFrom;
    }

    @JsonProperty("valueTo")
    public String getValueTo() {
        return valueTo;
    }

    @JsonProperty("valueTo")
    public void setValueTo(String valueTo) {
        this.valueTo = valueTo;
    }

    @JsonProperty("valueType")
    public String getValueType() {
        return valueType;
    }

    @JsonProperty("valueType")
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    @JsonProperty("validFor")
    public ValidFor getValidFor() {
        return validFor;
    }

    @JsonProperty("validFor")
    public void setValidFor(ValidFor validFor) {
        this.validFor = validFor;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
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

    @JsonProperty("stockItem.stockItemCharacteristicValue.value")
    public String getStockItemStockItemCharacteristicValueValue() {
        return stockItemStockItemCharacteristicValueValue;
    }

    @JsonProperty("stockItem.stockItemCharacteristicValue.value")
    public void setStockItemStockItemCharacteristicValueValue(String stockItemStockItemCharacteristicValueValue) {
        this.stockItemStockItemCharacteristicValueValue = stockItemStockItemCharacteristicValueValue;
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
