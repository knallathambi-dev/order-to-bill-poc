// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.util.List;

import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.UsageSpecification;

public final class InvalidUsageSelectedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final List<UsageSpecification> usageSpecifications;

	private InvalidUsageSelectedEvent() {
		productSpecId = null;
		usageSpecifications = null;
	}

	public InvalidUsageSelectedEvent(String productSpecId, List<UsageSpecification> usageSpecifications) {
		this.productSpecId = productSpecId;
		this.usageSpecifications = usageSpecifications;
	}

	@Override
	public String toString() {
		return "InvalidUsageSelectedEvent [productSpecId=" + productSpecId + ", usageSpecifications="
				+ usageSpecifications + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public List<UsageSpecification> getUsageSpecifications() {
		return usageSpecifications;
	}

}
