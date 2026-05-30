// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import java.time.OffsetDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ProductSpecificationDeleteCommmand {
	@TargetAggregateIdentifier
	private final String aggregateId;
	private final OffsetDateTime lastUpdateDateTime;
	private final Long interval;
	private final String intervalUnit;
	
	public ProductSpecificationDeleteCommmand(String aggregateId,OffsetDateTime lastUpdateDateTime, Long interval, String intervalUnit) {
		super();
		this.lastUpdateDateTime = lastUpdateDateTime;
		this.interval = interval;
		this.intervalUnit = intervalUnit;
		this.aggregateId = aggregateId;
	}
	
	public ProductSpecificationDeleteCommmand() {
		super();
		this.lastUpdateDateTime = null;
		this.interval = null;
		this.intervalUnit = null;
		this.aggregateId=null;
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

	public String getAggregateId() {
		return aggregateId;
	}

	@Override
	public String toString() {
		return "ProductSpecificationDeleteCommmand [aggregateId=" + aggregateId + ", lastUpdateDateTime="
				+ lastUpdateDateTime + ", interval=" + interval + ", intervalUnit=" + intervalUnit + "]";
	}
	
}
