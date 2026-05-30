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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;
public class ProductSpecModificationInitiatedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private final ProductSpecification productSpec;
	private final OffsetDateTime lastUpdate;

	private ProductSpecModificationInitiatedEvent() {
		productSpecId = null;
		productSpec = null;
		lastUpdate = null;
	}

	public ProductSpecModificationInitiatedEvent(String productSpecId, ProductSpecification productSpec,
			OffsetDateTime lastUpdate) {
		this.productSpecId = productSpecId;
		this.productSpec = productSpec;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ProductSpecModificationInitiatedEvent [productSpecId=" + productSpecId + ", productSpec=" + productSpec
				+ ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public ProductSpecification getProductSpec() {
		return productSpec;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
