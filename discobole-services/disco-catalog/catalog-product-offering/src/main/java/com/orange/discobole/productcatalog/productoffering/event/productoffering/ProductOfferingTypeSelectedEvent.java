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

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;

public class ProductOfferingTypeSelectedEvent implements ProductOfferingEvent {
	
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final OffsetDateTime lastUpdate;
	private final Boolean isSellable;
	private final Boolean isBundle;
	private final Boolean isInstallable;
	private final ProductOfferingType type;
	private final ProductOfferingLifecycle lifeCycleStatus;
	
	public ProductOfferingTypeSelectedEvent(String productOfferingId, OffsetDateTime lastUpdate, Boolean isSellable,
			Boolean isBundle, Boolean isInstallable, ProductOfferingType type,
			ProductOfferingLifecycle lifeCycleStatus) {
		super();
		this.productOfferingId = productOfferingId;
		this.lastUpdate = lastUpdate;
		this.isSellable = isSellable;
		this.isBundle = isBundle;
		this.isInstallable = isInstallable;
		this.type = type;
		this.lifeCycleStatus = lifeCycleStatus;
	}

	public ProductOfferingTypeSelectedEvent() {
		super();
		this.productOfferingId = null;
		this.lastUpdate = null;
		this.isSellable = null;
		this.isBundle = null;
		this.isInstallable = null;
		this.type = null;
		this.lifeCycleStatus = null;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public Boolean getIsSellable() {
		return isSellable;
	}

	public Boolean getIsBundle() {
		return isBundle;
	}

	public Boolean getIsInstallable() {
		return isInstallable;
	}

	public ProductOfferingType getType() {
		return type;
	}

	public ProductOfferingLifecycle getLifeCycleStatus() {
		return lifeCycleStatus;
	}

	@Override
	public String toString() {
		return "ProductOfferingTypeSelectedEvent [productOfferingId=" + productOfferingId + ", lastUpdate=" + lastUpdate
				+ ", isSellable=" + isSellable + ", isBundle=" + isBundle + ", isInstallable=" + isInstallable
				+ ", type=" + type + ", lifeCycleStatus=" + lifeCycleStatus + "]";
	}
}
