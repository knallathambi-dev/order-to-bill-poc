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

import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.category.dto.generated.productoffering.ProductOfferingLifecycle;

public class AtomicProductOfferingModificationValidatedEvent implements ProductOfferingEvent {
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final ProductOffering productOffering;
	private final ProductOfferingLifecycle lifecycleStatus;
	private final OffsetDateTime lastUpdate;

	/**
	 * @param productOfferingId
	 * @param productOffering
	 * @param lifecycleStatus
	 * @param lastUpdate
	 */
	public AtomicProductOfferingModificationValidatedEvent(String productOfferingId, ProductOffering productOffering,
			ProductOfferingLifecycle lifecycleStatus, OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.productOffering = productOffering;
		this.lifecycleStatus = lifecycleStatus;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingModificationValidatedEvent [productOfferingId=" + productOfferingId
				+ ", productOffering=" + productOffering + ", lifecycleStatus=" + lifecycleStatus + ", lastUpdate="
				+ lastUpdate + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public ProductOffering getProductOffering() {
		return productOffering;
	}

	public ProductOfferingLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
