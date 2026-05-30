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

import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.UsageSpecification;

public final class ProductSpecUsageSelectedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private final List<UsageSpecification> usageSpecs;

	private ProductSpecUsageSelectedEvent() {
		productSpecId = null;
		usageSpecs = null;
	}

	public ProductSpecUsageSelectedEvent(String productSpecId, List<UsageSpecification> usageSpecs) {
		this.productSpecId = productSpecId;
		this.usageSpecs = usageSpecs;
	}

	@Override
	public String toString() {
		return "ProductSpecUsageSelectedEvent [productSpecId=" + productSpecId + ", usageSpecs=" + usageSpecs + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public List<UsageSpecification> getUsageSpecs() {
		return usageSpecs;
	}
}
