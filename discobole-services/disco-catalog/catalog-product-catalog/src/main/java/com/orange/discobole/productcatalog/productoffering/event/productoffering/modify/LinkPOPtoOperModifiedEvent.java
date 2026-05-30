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

public class LinkPOPtoOperModifiedEvent implements ProductOfferingEvent {
	private final String prodOffId;
	private final List<CommercialOperation> operationList;
	private final List<ProductOfferingTerm> productOfferingTerm;
	private final OffsetDateTime lastUpdate;


	public LinkPOPtoOperModifiedEvent(){
		this.prodOffId = null;
		this.operationList = null;
		this.lastUpdate = null;
		this.productOfferingTerm = null;
	}

	public LinkPOPtoOperModifiedEvent(String prodOffId, List<CommercialOperation> operationList,
									  List<ProductOfferingTerm> productOfferingTerm,OffsetDateTime lastUpdate) {
		super();
		this.prodOffId = prodOffId;
		this.operationList = operationList;
		this.productOfferingTerm = productOfferingTerm;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "LinkPOPtoOperModifiedEvent{" +
				"prodOffId='" + prodOffId + '\'' +
				", operationList=" + operationList +
				", productOfferingTerm=" + productOfferingTerm +
				", lastUpdate=" + lastUpdate +
				'}';
	}

	public String getProdOffId() {
		return prodOffId;
	}

	public List<CommercialOperation> getOperationList() {
		return operationList;
	}

	public List<ProductOfferingTerm> getProductOfferingTerm() {
		return productOfferingTerm;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
