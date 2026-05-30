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

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

public class ProductOfferingModificationInitiatedEvent implements ProductOfferingEvent {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;

	private final ProductOffering productOffering;
	private final OffsetDateTime lastUpdate;
	private final ProductOfferingLifecycle lifecycle;

	/**
	 * @param productOfferingId
	 * @param productOffering
	 * @param lastUpdate
	 * @param lifecycle
	 */

	public ProductOfferingModificationInitiatedEvent(){
		this.productOfferingId = null;
		this.productOffering = null;
		this.lastUpdate = null;
		this.lifecycle = null;
	}

	public ProductOfferingModificationInitiatedEvent(String productOfferingId, ProductOffering productOffering,
			OffsetDateTime lastUpdate, ProductOfferingLifecycle lifecycle) {
		this.productOfferingId = productOfferingId;
		this.productOffering = productOffering;
		this.lastUpdate = lastUpdate;
		this.lifecycle = lifecycle;
	}

	@Override
	public String toString() {
		return "ProductOfferingModificationInitiatedEvent [productOfferingId=" + productOfferingId
				+ ", productOffering=" + productOffering + ", lastUpdate=" + lastUpdate + ", lifecycle=" + lifecycle
				+ "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public ProductOffering getProductOffering() {
		return productOffering;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public ProductOfferingLifecycle getLifecycle() {
		return lifecycle;
	}

}
