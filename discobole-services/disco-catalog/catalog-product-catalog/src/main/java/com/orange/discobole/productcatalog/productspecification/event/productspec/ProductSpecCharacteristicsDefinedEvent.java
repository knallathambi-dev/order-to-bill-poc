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

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.UsageSpecification;

/**
 * Event that will be raised when product specification characteristics is valid
 * and get configured.
 *
 * @author Vivek Singh
 * @since 1.0
 *
 */
public final class ProductSpecCharacteristicsDefinedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final List<ProductSpecificationCharacteristic> productSpecificationCharacteristics;
	private List<UsageSpecification> usageSpec;
	private final OffsetDateTime lastUpdate;

	private ProductSpecCharacteristicsDefinedEvent() {
		productSpecId = null;
		productSpecificationCharacteristics = null;
		lastUpdate = null;
		usageSpec = null;
	}

	public ProductSpecCharacteristicsDefinedEvent(String productSpecId,
			List<ProductSpecificationCharacteristic> productSpecificationCharacteristics,List<UsageSpecification> usageSpec, OffsetDateTime lastUpdate) {
		this.productSpecId = productSpecId;
		this.productSpecificationCharacteristics = productSpecificationCharacteristics;
		this.usageSpec=usageSpec;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ProductSpecCharacteristicsDefinedEvent [" + "productSpecId=" + productSpecId
				+ ", productSpecificationCharacteristics=" + productSpecificationCharacteristics
				+ ", usageSpec="+usageSpec
				+ ", lastUpdate="+ lastUpdate + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public List<ProductSpecificationCharacteristic> getProductSpecificationCharacteristics() {
		return productSpecificationCharacteristics;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public List<UsageSpecification> getUsageSpec() {
		return usageSpec;
	}

}
