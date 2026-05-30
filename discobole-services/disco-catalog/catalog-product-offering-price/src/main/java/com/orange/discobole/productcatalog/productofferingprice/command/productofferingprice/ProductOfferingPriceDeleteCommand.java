// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice;

import java.time.OffsetDateTime;

public class ProductOfferingPriceDeleteCommand {
	private final OffsetDateTime lastUpdateDateTime;
	private final Long interval;
	private final String intervalUnit;
	
	
	public ProductOfferingPriceDeleteCommand(OffsetDateTime lastUpdateDateTime, Long interval, String intervalUnit) {
		super();
		this.lastUpdateDateTime = lastUpdateDateTime;
		this.interval = interval;
		this.intervalUnit = intervalUnit;
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
		return "ProductOfferingPriceDeleteCommand [lastUpdateDateTime=" + lastUpdateDateTime + ", interval=" + interval
				+ ", intervalUnit=" + intervalUnit + "]";
	}
	
}
