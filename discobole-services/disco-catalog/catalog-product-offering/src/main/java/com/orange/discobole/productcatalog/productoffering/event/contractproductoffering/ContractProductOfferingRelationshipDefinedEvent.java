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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;

/**
 * Event raised to show the all valid selected relationship from the user, that
 * can be defined. Class ContractProductOfferingRelationshipDefinedEvent
 * 
 * @author Vishal Vachaspati
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractProductOfferingRelationshipDefinedEvent implements ContractProductOfferingEvent {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<ProductOfferingRelationship> productOfferingRelationships;
	private final OffsetDateTime lastUpdate;

	
	public ContractProductOfferingRelationshipDefinedEvent() {
		super();
		this.productOfferingId = null;
		this.productOfferingRelationships = null;
		this.lastUpdate = null;
	}
	

	/**
	 * @param productOfferingId
	 * @param productOfferingRelationships
	 * @param lastUpdate
	 */
	public ContractProductOfferingRelationshipDefinedEvent(String productOfferingId,
			List<ProductOfferingRelationship> productOfferingRelationships, OffsetDateTime lastUpdate) {
		super();
		this.productOfferingId = productOfferingId;
		this.productOfferingRelationships = productOfferingRelationships;
		this.lastUpdate = lastUpdate;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public List<ProductOfferingRelationship> getProductOfferingRelationships() {
		return productOfferingRelationships;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}


	@Override
	public String toString() {
		return "ContractProductOfferingRelationshipDefinedEvent [productOfferingId=" + productOfferingId
				+ ", productOfferingRelationships=" + productOfferingRelationships + ", lastUpdate=" + lastUpdate
				+ "]";
	}

}
