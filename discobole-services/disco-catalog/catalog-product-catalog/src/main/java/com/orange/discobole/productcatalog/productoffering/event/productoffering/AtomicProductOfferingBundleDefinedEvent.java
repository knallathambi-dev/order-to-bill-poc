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

/**
 * The Class AtomicProductOfferingBundleDefinedEvent sets isBundle property to
 * false.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class AtomicProductOfferingBundleDefinedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final Boolean isBundle;
	private final OffsetDateTime lastUpdate;

	private AtomicProductOfferingBundleDefinedEvent() {
		this.productOfferingId = null;
		this.isBundle = null;
		this.lastUpdate = null;
	}

	public AtomicProductOfferingBundleDefinedEvent(String productOfferingId, Boolean isBundle,
			OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.isBundle = isBundle;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingBundleDefinedEvent [productOfferingId=" + productOfferingId + ", isBundle="
				+ isBundle + ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public Boolean isIsBundle() {
		return isBundle;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
