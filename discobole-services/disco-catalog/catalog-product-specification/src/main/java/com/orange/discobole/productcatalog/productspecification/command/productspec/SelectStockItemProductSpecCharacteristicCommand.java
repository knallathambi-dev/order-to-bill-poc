// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristic;

public class SelectStockItemProductSpecCharacteristicCommand {

	@TargetAggregateIdentifier
	private final String productSpecId;
    private final List<ProductSpecificationCharacteristic> stockItemCharacteristics;

    public SelectStockItemProductSpecCharacteristicCommand(String productSpecId,List<ProductSpecificationCharacteristic> stockItemCharacteristics) {
        this.stockItemCharacteristics = stockItemCharacteristics;
		this.productSpecId = productSpecId;
    }

    public List<ProductSpecificationCharacteristic> getProductSpecCharacteristics() {
        return stockItemCharacteristics;
    }

	@Override
	public String toString() {
		return "SelectStockItemProductSpecCharacteristicCommand{" + "productSpecId=" + productSpecId
				+ ", productSpecCharacteristics=" + stockItemCharacteristics + '}';
	}

	/**
	 * @return the productSpecId
	 */
	public String getProductSpecId() {
		return productSpecId;
	}
    
}

