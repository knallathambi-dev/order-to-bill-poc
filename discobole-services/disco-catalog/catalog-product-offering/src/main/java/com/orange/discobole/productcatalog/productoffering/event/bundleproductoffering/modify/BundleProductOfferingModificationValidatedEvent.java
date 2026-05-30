// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

import java.time.OffsetDateTime;

public class BundleProductOfferingModificationValidatedEvent implements ProductOfferingEvent {
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final ProductOffering productOffering;
	private final ProductOfferingLifecycle lifecycleStatus;
	private final OffsetDateTime lastUpdate;
	private final String version;

	/**
	 * @param productOfferingId
	 * @param lifecycleStatus
	 * @param lastUpdate
	 */
	public BundleProductOfferingModificationValidatedEvent(String productOfferingId, ProductOfferingLifecycle lifecycleStatus, OffsetDateTime lastUpdate, String version) {
		this.productOfferingId = productOfferingId;
		this.lifecycleStatus = lifecycleStatus;
		this.lastUpdate = lastUpdate;
		this.version = version;
		this.productOffering = null;
	}

	public BundleProductOfferingModificationValidatedEvent() {
		this.productOfferingId = null;
		this.lifecycleStatus = null;
		this.lastUpdate = null;
		this.version = null;
		this.productOffering = null;
	}

    public BundleProductOfferingModificationValidatedEvent(String productOfferingId, ProductOffering storedProductOffering, ProductOfferingLifecycle lifecycleStatus, OffsetDateTime lastUpdate, String version) {
		this.productOfferingId = productOfferingId;
		this.lifecycleStatus = lifecycleStatus;
		this.lastUpdate = lastUpdate;
		this.version = version;
		this.productOffering = storedProductOffering;
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
		return "BundleProductOfferingModificationValidatedEvent{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", lifecycleStatus=" + lifecycleStatus +
				", lastUpdate=" + lastUpdate +
				", version='" + version + '\'' +
				'}';
	}
}
