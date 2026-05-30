// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;

public final class BundleProductOfferingOperDefinedEvent implements BundleProductOfferingEvent{
   
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<CommercialOperation> operationSpecifications;
	private final OffsetDateTime lastUpdate;

	/**
	 * @param productOfferingId
	 * @param operationSpecifications
	 * @param lastUpdate
	 */
	public BundleProductOfferingOperDefinedEvent(String productOfferingId,
			List<CommercialOperation> operationSpecifications, OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.operationSpecifications = operationSpecifications;
		this.lastUpdate = lastUpdate;
	}
	
	public BundleProductOfferingOperDefinedEvent() {
		this.productOfferingId = null;
		this.operationSpecifications = null;
		this.lastUpdate = null;
	}

	@Override
	public String toString() {
		return "BundleProductOfferingOperDefinedEvent [productOfferingId=" + productOfferingId
				+ ", operationSpecifications=" + operationSpecifications + ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public List<CommercialOperation> getOperationSpecifications() {
		return operationSpecifications;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
