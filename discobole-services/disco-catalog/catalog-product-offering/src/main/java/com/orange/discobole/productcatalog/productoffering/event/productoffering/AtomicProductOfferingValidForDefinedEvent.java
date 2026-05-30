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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;

public class AtomicProductOfferingValidForDefinedEvent implements ProductOfferingEvent {
	@TargetAggregateIdentifier
	private final String productOffId;
	private final TimePeriod validFor;
	private final OffsetDateTime lastUpdate;

	private AtomicProductOfferingValidForDefinedEvent() {
		this.productOffId = null;
		this.validFor = null;
		this.lastUpdate = null;
	}

	public AtomicProductOfferingValidForDefinedEvent(String productOffId, TimePeriod validFor,
			OffsetDateTime lastUpdate) {
		this.productOffId = productOffId;
		this.validFor = validFor;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ProductSpecValidForDefinedEvent [productOffId=" + productOffId + ", validFor=" + validFor
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
