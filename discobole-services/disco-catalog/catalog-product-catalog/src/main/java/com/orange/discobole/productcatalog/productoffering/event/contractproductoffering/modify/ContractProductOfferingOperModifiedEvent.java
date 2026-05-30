// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify;


import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.CommercialOperation;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingEvent;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * ContractProductOfferingOperDefinedEvent Class.
 * @author BMKJ8547
 *
 */
public final class ContractProductOfferingOperModifiedEvent implements ContractProductOfferingEvent {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<CommercialOperation> operationSpecifications;
	private final OffsetDateTime lastUpdate;

	/**
	 * @param productOfferingId
	 * @param operationSpecifications
	 * @param lastUpdate
	 */
	public ContractProductOfferingOperModifiedEvent(String productOfferingId,
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
