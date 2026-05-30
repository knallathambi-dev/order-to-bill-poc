// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.processflow.annotation.ReadOnly;

/**
 * @author Varshika Choudhary
 * @since 1.0
 */
@Array
public class CharacteristicValueSpecification {

    @ReadOnly(true)
    private String unitOfMeasure;
    private TimePeriod validFor;
    private TimeRange timeRange;
    @ReadOnly(true)
    private String value;
    private String valueFrom;
    private String valueTo;
    private String valueType;
    @JsonProperty("stockItem.stockItemCharacteristicValue.value")
    private String stockItemCharacteristicValueReference;

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public TimePeriod getValidFor() {
        return validFor;
    }

    public void setValidFor(TimePeriod validFor) {
        this.validFor = validFor;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(String valueFrom) {
        this.valueFrom = valueFrom;
    }

    public String getValueTo() {
        return valueTo;
    }

    public void setValueTo(String valueTo) {
        this.valueTo = valueTo;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getStockItemCharacteristicValueReference() {
        return stockItemCharacteristicValueReference;
    }

    public void setStockItemCharacteristicValueReference(String stockItemCharacteristicValueReference) {
        this.stockItemCharacteristicValueReference = stockItemCharacteristicValueReference;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }
}
