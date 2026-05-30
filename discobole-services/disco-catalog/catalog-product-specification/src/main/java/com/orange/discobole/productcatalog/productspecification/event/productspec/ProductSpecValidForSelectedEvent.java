// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;

public class ProductSpecValidForSelectedEvent implements ProductSpecEvent {

	
	@TargetAggregateIdentifier
	private final String productSpecId;
	private final TimePeriod validFor;
	
	private ProductSpecValidForSelectedEvent() {
		this.productSpecId = null;
		this.validFor = null;
	}

	public ProductSpecValidForSelectedEvent(String productSpecId, TimePeriod validFor) {
		this.productSpecId = productSpecId;
		this.validFor = validFor;
	}

	@Override
	public String toString() {
		return "ProductSpecValidForSelectedEvent [productSpecId=" + productSpecId
				+ ", validFor=" + validFor + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}
}
