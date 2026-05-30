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

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;

/**
 * The Class AtomicProductOfferingValidatedEvent stores the updated lifecycle
 * status of product offering when the creation is complete.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class AtomicProductOfferingValidatedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final ProductOfferingLifecycle lifecycleStatus;
	private final OffsetDateTime lastUpdate;

	private AtomicProductOfferingValidatedEvent() {
		this.productOfferingId = null;
		this.lifecycleStatus = null;
		this.lastUpdate=null;
	}

	public AtomicProductOfferingValidatedEvent(String productOfferingId, ProductOfferingLifecycle lifecycleStatus,
											   OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.lifecycleStatus = lifecycleStatus;
		this.lastUpdate=lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingValidatedEvent{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", lifecycleStatus=" + lifecycleStatus +
				", lastUpdate=" + lastUpdate +
				'}';
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
