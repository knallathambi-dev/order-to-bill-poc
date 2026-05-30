// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import java.time.OffsetDateTime;
import java.util.List;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.CommercialOperation;

public class AtomicProductOfferingOperDefinedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final List<CommercialOperation> operationSpecifications;
	private final List<ProductOfferingTerm> productOfferingTerm;
	private final OffsetDateTime lastUpdate;

	private AtomicProductOfferingOperDefinedEvent() {
		this.productOfferingId = null;
		this.operationSpecifications = null;
		this.lastUpdate = null;
		this.productOfferingTerm = null;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingOperDefinedEvent{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", operationSpecifications=" + operationSpecifications +
				", productOfferingTerm=" + productOfferingTerm +
				", lastUpdate=" + lastUpdate +
				'}';
	}

	public AtomicProductOfferingOperDefinedEvent(String productOfferingId, List<CommercialOperation> operationSpecifications, List<ProductOfferingTerm> productOfferingTerm, OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.operationSpecifications = operationSpecifications;
		this.lastUpdate = lastUpdate;
		this.productOfferingTerm = productOfferingTerm;
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

	public List<ProductOfferingTerm> getProductOfferingTerm() {
		return productOfferingTerm;
	}
}
