// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.dto.generated.stockitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.ReadOnly;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;

import io.swagger.annotations.ApiModel;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotBlank;

/**
 * A StockItemCharacteristicValue object is used to define a set of
 * attributes, each of which can be assigned to a corresponding set of
 * attributes in a StockItemCharacteristic object. The values of the
 * attributes in the StockItemCharacteristicValue object describe the values
 * of the attributes that a corresponding StockItemCharacteristic object can
 * take on.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
@ApiModel(description = "A StockItemCharacteristicValue object is used to define a set of attributes, each of which can be  assigned to a corresponding set of attributes in a StockItemCharacteristic object. The values of the attributes in the StockItemCharacteristicValue object describe the values of the attributes that a corresponding StockItemCharacteristic object can take on.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-10-08T11:08:21.439+05:30")
public class StockItemCharacteristicValue {
    @NotBlank
    @JsonProperty("@type")
    @ReadOnly(true)
    private String type;
    @JsonProperty("value")
    private String value;
    private TimePeriod validFor;
    private StockItemCharacteristic stockItemCharacteristic;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TimePeriod getValidFor() {
        return validFor;
    }

    public void setValidFor(TimePeriod validFor) {
        this.validFor = validFor;
    }

    public StockItemCharacteristic getStockItemCharacteristic() {
        return stockItemCharacteristic;
    }

    public void setStockItemCharacteristic(StockItemCharacteristic stockItemCharacteristic) {
        this.stockItemCharacteristic = stockItemCharacteristic;
    }
}