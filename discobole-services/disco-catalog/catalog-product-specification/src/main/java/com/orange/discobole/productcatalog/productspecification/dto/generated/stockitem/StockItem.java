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
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.StockItemType;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * The stock item allows for a tangible configured product to find the article in the stock.
 * Stock Item is used to manage an abstraction layer between Product Specification and Physical resource Specification.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class StockItem {
    @NotBlank
    @ReadOnly(true)
    private String id;
    private String name;
    private String description;
    private TimePeriod validFor;
    private String state;
    @JsonProperty("EAN")
    private String EAN;
    @JsonProperty("GTIN")
    private String GTIN;
    private StockItemType stockItemType;
    private List<StockItemCharacteristicValue> stockItemCharacteristicValue;
    private OffsetDateTime lastUpdate;
    
    @Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StockItem StockItem = (StockItem) o;
		return Objects.equals(id, StockItem.id) && Objects.equals(name, StockItem.name)
				&& Objects.equals(description, StockItem.description)
				&& Objects.equals(validFor, StockItem.validFor)
				&& Objects.equals(state, StockItem.state)
				&& Objects.equals(EAN, StockItem.EAN)
				&& Objects.equals(GTIN, StockItem.GTIN)
				&& Objects.equals(stockItemType, StockItem.stockItemType)
				&& Objects.equals(stockItemCharacteristicValue, StockItem.stockItemCharacteristicValue)
				&& Objects.equals(lastUpdate, StockItem.lastUpdate);
	}
    
   @Override
	public int hashCode() {
		
		return Objects.hash(id,name, description,validFor,state,EAN,GTIN,stockItemType,stockItemCharacteristicValue,lastUpdate);
	}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TimePeriod getValidFor() {
        return validFor;
    }

    public void setValidFor(TimePeriod validFor) {
        this.validFor = validFor;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEAN() {
        return EAN;
    }

    public void setEAN(String EAN) {
        this.EAN = EAN;
    }

    public String getGTIN() {
        return GTIN;
    }

    public void setGTIN(String GTIN) {
        this.GTIN = GTIN;
    }

    public StockItemType getStockItemType() {
        return stockItemType;
    }

    public void setStockItemType(StockItemType stockItemType) {
        this.stockItemType = stockItemType;
    }

    public List<StockItemCharacteristicValue> getStockItemCharacteristicValue() {
        return stockItemCharacteristicValue;
    }

    public void setStockItemCharacteristicValue(List<StockItemCharacteristicValue> stockItemCharacteristicValue) {
        this.stockItemCharacteristicValue = stockItemCharacteristicValue;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


}
