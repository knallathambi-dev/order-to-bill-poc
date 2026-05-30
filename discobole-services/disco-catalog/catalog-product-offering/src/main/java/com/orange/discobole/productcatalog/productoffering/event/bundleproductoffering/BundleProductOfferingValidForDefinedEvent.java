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

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;

/**
 * The Class BundleProductOfferingValidForDefinedEvent.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class BundleProductOfferingValidForDefinedEvent implements BundleProductOfferingEvent {
	@TargetAggregateIdentifier
	private final String productOffId;
	private final TimePeriod validFor;
	private final OffsetDateTime lastUpdate;

	/**
	 * @param productOffId
	 * @param validFor
	 * @param lastUpdate
	 */
	public BundleProductOfferingValidForDefinedEvent(String productOffId, TimePeriod validFor,
			OffsetDateTime lastUpdate) {
		super();
		this.productOffId = productOffId;
		this.validFor = validFor;
		this.lastUpdate = lastUpdate;
	}

	public BundleProductOfferingValidForDefinedEvent() {
		super();
		this.productOffId = null;
		this.validFor = null;
		this.lastUpdate = null;
	}

	@Override
	public String toString() {
		return "BundleProductOfferingValidForDefinedEvent [productOffId=" + productOffId + ", validFor=" + validFor
				+ ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductOffId() {
		return productOffId;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
