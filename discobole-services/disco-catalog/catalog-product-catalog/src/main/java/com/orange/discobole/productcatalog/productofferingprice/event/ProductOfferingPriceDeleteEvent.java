// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event;

import java.time.OffsetDateTime;

public class ProductOfferingPriceDeleteEvent implements ProductOfferingPriceEvent {

	private final String aggregateId;
	private final OffsetDateTime lastUpdateDateTime;
	private final Long interval;
	private final String intervalUnit;
	
	public ProductOfferingPriceDeleteEvent(String aggregateId, OffsetDateTime lastUpdateDateTime, Long interval,
			String intervalUnit) {
		super();
		this.aggregateId = aggregateId;
		this.lastUpdateDateTime = lastUpdateDateTime;
		this.interval = interval;
		this.intervalUnit = intervalUnit;
	}
	
	public ProductOfferingPriceDeleteEvent() {
		super();
		this.aggregateId = null;
		this.lastUpdateDateTime = null;
		this.interval = null;
		this.intervalUnit = null;
	}

	public String getAggregateId() {
		return aggregateId;
	}
	public OffsetDateTime getLastUpdateDateTime() {
		return lastUpdateDateTime;
	}
	
	public Long getInterval() {
		return interval;
	}
	
	public String getIntervalUnit() {
		return intervalUnit;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceDeleteEvent [aggregateId=" + aggregateId + ", lastUpdateDateTime="
				+ lastUpdateDateTime + ", interval=" + interval + ", intervalUnit=" + intervalUnit + "]";
	}

	
	
	
}
