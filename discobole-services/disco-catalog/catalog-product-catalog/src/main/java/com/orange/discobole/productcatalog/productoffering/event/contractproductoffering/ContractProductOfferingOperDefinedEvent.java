// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.contractproductoffering;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.CommercialOperation;

/**
 * ContractProductOfferingOperDefinedEvent Class.
 * @author BMKJ8547
 *
 */
public final class ContractProductOfferingOperDefinedEvent implements ContractProductOfferingEvent{
   
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<CommercialOperation> operationSpecifications;
	private final OffsetDateTime lastUpdate;

	
	public ContractProductOfferingOperDefinedEvent() {
		this.productOfferingId = null;
		this.operationSpecifications = null;
		this.lastUpdate =  null;
	}

	
	/**
	 * @param productOfferingId
	 * @param operationSpecifications
	 * @param lastUpdate
	 */
	public ContractProductOfferingOperDefinedEvent(String productOfferingId,
			List<CommercialOperation> operationSpecifications, OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.operationSpecifications = operationSpecifications;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ContractProductOfferingOperDefinedEvent [productOfferingId=" + productOfferingId
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
