// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.UsageSpecification;

public class ProductSpecCharacteristicsModifiedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private final List<ProductSpecificationCharacteristic> productSpecificationCharacteristics;
	private final List<UsageSpecification> usageSpecifications;
	private final OffsetDateTime lastUpdate;

	private ProductSpecCharacteristicsModifiedEvent() {
		productSpecId = null;
		productSpecificationCharacteristics = null;
		usageSpecifications=null;
		lastUpdate = null;
	}

	public ProductSpecCharacteristicsModifiedEvent(String productSpecId,
			List<ProductSpecificationCharacteristic> productSpecificationCharacteristics,List<UsageSpecification> usageSpecifications, OffsetDateTime lastUpdate) {
		this.productSpecId = productSpecId;
		this.productSpecificationCharacteristics = productSpecificationCharacteristics;
		this.usageSpecifications = usageSpecifications;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ProductSpecCharacteristicsModifiedEvent [productSpecId=" + productSpecId
				+ ", productSpecificationCharacteristics=" + productSpecificationCharacteristics
				+ ", usageSpecifications=" + usageSpecifications+ ", lastUpdate="
				+ lastUpdate + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public List<ProductSpecificationCharacteristic> getProductSpecificationCharacteristics() {
		return productSpecificationCharacteristics;
	}

	public List<UsageSpecification> getUsageSpecifications() {
		return usageSpecifications;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
