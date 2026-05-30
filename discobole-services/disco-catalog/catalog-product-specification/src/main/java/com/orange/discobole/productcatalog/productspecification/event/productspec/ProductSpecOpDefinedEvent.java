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

import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.OperationSpecification;

public class ProductSpecOpDefinedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private final List<OperationSpecification> operationSpecifications;
	private final OffsetDateTime lastUpdate;

	private ProductSpecOpDefinedEvent() {
		productSpecId = null;
		operationSpecifications = null;
		lastUpdate = null;
	}

	public ProductSpecOpDefinedEvent(String productSpecId, List<OperationSpecification> operationSpecifications,
			OffsetDateTime lastUpdate) {

		this.productSpecId = productSpecId;
		this.operationSpecifications = operationSpecifications;
		this.lastUpdate = lastUpdate;
	}
	
	

	@Override
	public String toString() {
		return "ProductSpecOpDefinedEvent [productSpecId=" + productSpecId + ", operationSpecifications="
				+ operationSpecifications + ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public List<OperationSpecification> getOperationSpecifications() {
		return operationSpecifications;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}
	
}
