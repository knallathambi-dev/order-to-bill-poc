// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering;

import java.time.OffsetDateTime;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;

/**
 * The Class BundleProductOfferingValidatedEvent stores the updated lifecycle
 * status of bundle product offering when the creation is complete.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class BundleProductOfferingValidatedEvent implements BundleProductOfferingEvent {

	private final String productOfferingId;
	private final ProductOfferingLifecycle lifecycleStatus;
	private final OffsetDateTime lastUpdate;

	public BundleProductOfferingValidatedEvent() {
		super();
		this.productOfferingId = null;
		this.lifecycleStatus = null;
		this.lastUpdate = null;
	}
	
	/**
	 * @param productOfferingId
	 * @param lifecycleStatus
	 * @param lastUpdate
	 */
	public BundleProductOfferingValidatedEvent(String productOfferingId, ProductOfferingLifecycle lifecycleStatus,
			OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.lifecycleStatus = lifecycleStatus;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "BundleProductOfferingValidatedEvent [productOfferingId=" + productOfferingId + ", lifecycleStatus="
				+ lifecycleStatus + ", lastUpdate=" + lastUpdate + "]";
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

}
