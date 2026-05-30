// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering.modify;

import java.time.OffsetDateTime;
import java.util.List;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.CommercialOperation;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

/**
 * Event raised to modify the PO Operations from the user.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class AtomicProductOfferingOperationModifiedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final List<CommercialOperation> operationSpecifications;
	private final List<ProductOfferingTerm> productOfferingTerm;
	private final OffsetDateTime lastUpdate;

	public AtomicProductOfferingOperationModifiedEvent(){
		this.productOfferingId = null;
		this.operationSpecifications = null;
		this.lastUpdate = null;
		this.productOfferingTerm = null;
	}

	/**
	 *
	 * @param productOfferingId
	 * @param operationSpecifications
	 * @param productOfferingTerm
	 * @param lastUpdate
	 */
	public AtomicProductOfferingOperationModifiedEvent(String productOfferingId,
													   List<CommercialOperation> operationSpecifications,List<ProductOfferingTerm> productOfferingTerm, OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.operationSpecifications = operationSpecifications;
		this.productOfferingTerm = productOfferingTerm;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingOperationModifiedEvent{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", operationSpecifications=" + operationSpecifications +
				", productOfferingTerm=" + productOfferingTerm +
				", lastUpdate=" + lastUpdate +
				'}';
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
