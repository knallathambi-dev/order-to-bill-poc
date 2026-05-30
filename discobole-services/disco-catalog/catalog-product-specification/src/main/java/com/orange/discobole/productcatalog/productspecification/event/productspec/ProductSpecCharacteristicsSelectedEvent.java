// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristic;

public class ProductSpecCharacteristicsSelectedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
    private final String productSpecId;
    private final List<ProductSpecificationCharacteristic> productSpecificationCharacteristics;

    private ProductSpecCharacteristicsSelectedEvent() {
        this.productSpecId = null;
        this.productSpecificationCharacteristics = null;
    }

    public ProductSpecCharacteristicsSelectedEvent(String productSpecId,
                                                   List<ProductSpecificationCharacteristic> productSpecificationCharacteristics) {
        this.productSpecId = productSpecId;
        this.productSpecificationCharacteristics = productSpecificationCharacteristics;
    }


    public String getProductSpecId() {
        return productSpecId;
    }

    public List<ProductSpecificationCharacteristic> getProductSpecificationCharacteristics() {
        return productSpecificationCharacteristics;
    }

	@Override
	public String toString() {
		return "ProductSpecCharacteristicsSelectedEvent{" + "productSpecId='" + productSpecId
				+ ", productSpecificationCharacteristics=" + productSpecificationCharacteristics + '}';
	}

    
}
