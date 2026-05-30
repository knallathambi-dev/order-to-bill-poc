// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

import java.time.OffsetDateTime;

public class ContractProductOfferingModificationValidatedEvent implements ProductOfferingEvent {
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final ProductOffering productOffering;
	private final ProductOfferingLifecycle lifecycleStatus;
	private final OffsetDateTime lastUpdate;
	private final String version;

	
	public ContractProductOfferingModificationValidatedEvent() {
		this.productOfferingId = null;
		this.lifecycleStatus = null;
		this.lastUpdate = null;
		this.version = null;
		this.productOffering = null;
	}
	/**
	 * @param productOfferingId
	 * @param lifecycleStatus
	 * @param lastUpdate
	 */
	public ContractProductOfferingModificationValidatedEvent(String productOfferingId, ProductOfferingLifecycle lifecycleStatus, OffsetDateTime lastUpdate, String version) {
		this.productOfferingId = productOfferingId;
		this.lifecycleStatus = lifecycleStatus;
		this.lastUpdate = lastUpdate;
		this.version = version;
		this.productOffering = null;
	}

    public ContractProductOfferingModificationValidatedEvent(String productOfferingId, ProductOffering storedProductOffering, ProductOfferingLifecycle lifecycleStatus, OffsetDateTime lastUpdate, String version) {
		this.productOfferingId = productOfferingId;
		this.productOffering = storedProductOffering;
		this.lifecycleStatus = lifecycleStatus;
		this.lastUpdate = lastUpdate;
		this.version = version;
	}

    public String getProductOfferingId() {
		return productOfferingId;
	}

	public ProductOfferingLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public String getVersion() {
		return version;
	}

	public ProductOffering getProductOffering() {
		return productOffering;
	}

	@Override
	public String toString() {
		return "ContractProductOfferingModificationValidatedEvent{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", lifecycleStatus=" + lifecycleStatus +
				", lastUpdate=" + lastUpdate +
				", version='" + version + '\'' +
				'}';
	}
}
