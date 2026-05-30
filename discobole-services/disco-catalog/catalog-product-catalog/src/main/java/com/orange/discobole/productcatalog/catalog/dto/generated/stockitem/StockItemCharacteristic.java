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

import jakarta.annotation.Generated;
import io.swagger.annotations.ApiModel;
import jakarta.validation.constraints.NotBlank;

/**
 * The StockItemCharacteristic is characteristic to specify the stock item type attributes
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
@ApiModel(description = "The StockItemCharacteristic is characteristic to specify the stock item type attributes")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-10-08T11:08:21.439+05:30")
public class StockItemCharacteristic {
    @NotBlank
    @ReadOnly(true)
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    private TimePeriod validFor;
    private String description;
    private String valueType;
    @JsonProperty("@type")
    private String type = "StringCharacteristic";
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public TimePeriod getValidFor() {
        return validFor;
    }

    public void setValidFor(TimePeriod validFor) {
        this.validFor = validFor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

}