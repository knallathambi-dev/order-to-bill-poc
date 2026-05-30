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

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.category.dto.generated.productoffering.CommercialOperation;

public class LinkPOPtoOperModifiedEvent implements ProductOfferingEvent {
	@TargetAggregateIdentifier
	private final String prodOffId;
	private final List<CommercialOperation> operationList;
	private final OffsetDateTime lastUpdate;

	/**
	 * @param prodOffId
	 * @param operationList
	 * @param lastUpdate
	 */
	public LinkPOPtoOperModifiedEvent(String prodOffId, List<CommercialOperation> operationList,
			OffsetDateTime lastUpdate) {
		super();
		this.prodOffId = prodOffId;
		this.operationList = operationList;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "LinkPOPtoOperModifiedEvent [prodOffId=" + prodOffId + ", operationList=" + operationList
				+ ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProdOffId() {
		return prodOffId;
	}

	public List<CommercialOperation> getOperationList() {
		return operationList;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
