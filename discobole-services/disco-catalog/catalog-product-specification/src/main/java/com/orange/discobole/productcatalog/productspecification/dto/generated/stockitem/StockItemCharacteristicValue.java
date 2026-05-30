// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.ReadOnly;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;

import io.swagger.annotations.ApiModel;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * @author Varshika Choudhary
 * @since 1.0
 */
@ApiModel(description = "A StockItemCharacteristicValue object is used to define a set of attributes, each of which can be assigned to a corresponding set of attributes in a StockItemCharacteristic object.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-07-30T16:59:05.795+05:30")
public class StockItemCharacteristicValue {
    @NotBlank
    @JsonProperty("@type")
    @ReadOnly(true)
    private String type;
    private String value;
    private TimePeriod validFor;
    private StockItemCharacteristic stockItemCharacteristic;

    public StockItemCharacteristicValue type(String type) {
        this.type = type;
        return this;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StockItemCharacteristicValue value(String value) {
        this.value = value;
        return this;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StockItemCharacteristicValue validFor(TimePeriod validFor) {
        this.validFor = validFor;
        return this;
    }
    public TimePeriod getValidFor() {
        return validFor;
    }

    public void setValidFor(TimePeriod validFor) {
        this.validFor = validFor;
    }

    public StockItemCharacteristicValue stockItemCharacteristic(StockItemCharacteristic stockItemCharacteristic) {
        this.stockItemCharacteristic = stockItemCharacteristic;
        return this;
    }
    public StockItemCharacteristic getStockItemCharacteristic() {
        return stockItemCharacteristic;
    }

    public void setStockItemCharacteristic(StockItemCharacteristic stockItemCharacteristic) {
        this.stockItemCharacteristic = stockItemCharacteristic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StockItemCharacteristicValue stockItemCharacteristicValue = (StockItemCharacteristicValue) o;
        return Objects.equals(this.validFor, stockItemCharacteristicValue.validFor)
                && Objects.equals(this.value, stockItemCharacteristicValue.value)
                && Objects.equals(this.stockItemCharacteristic, stockItemCharacteristicValue.stockItemCharacteristic)
                && Objects.equals(this.type, stockItemCharacteristicValue.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validFor, value, stockItemCharacteristic, type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class StockItemCharacteristicValue {\n");

        sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("    stockItemCharacteristic: ").append(toIndentedString(stockItemCharacteristic)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
