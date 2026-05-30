// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.dto;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;

public class DefinePOPStatusValidityPeriod {

	private ProductOfferingPriceLifecycle lifecycleStatus;
	private TimePeriod validFor;

	@Override
	public String toString() {
		return "DefinePOPStatusValidityPeriod{" + "lifecycleStatus=" + lifecycleStatus + ", validFor=" + validFor + '}';
	}

	public DefinePOPStatusValidityPeriod lifecycleStatus(ProductOfferingPriceLifecycle lifecycleStatus) {
		this.lifecycleStatus = lifecycleStatus;
		return this;
	}

	public ProductOfferingPriceLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

	public void setLifecycleStatus(ProductOfferingPriceLifecycle lifecycleStatus) {
		this.lifecycleStatus = lifecycleStatus;
	}

	public DefinePOPStatusValidityPeriod validFor(TimePeriod validFor) {
		this.validFor = validFor;
		return this;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}
}
