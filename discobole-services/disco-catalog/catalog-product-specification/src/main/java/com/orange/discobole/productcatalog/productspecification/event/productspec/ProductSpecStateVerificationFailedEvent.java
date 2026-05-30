// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;

public class ProductSpecStateVerificationFailedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final ProductSpecificationLifecycle lifecycleStatus;

	private ProductSpecStateVerificationFailedEvent() {
		productSpecId = null;
		lifecycleStatus = null;
	}

	public ProductSpecStateVerificationFailedEvent(String productSpecId,
			ProductSpecificationLifecycle lifecycleStatus) {
		this.productSpecId = productSpecId;
		this.lifecycleStatus = lifecycleStatus;
	}

	@Override
	public String toString() {
		return "ProductSpecStateVerificationFailedEvent [productSpecId=" + productSpecId + ", lifecycleStatus="
				+ lifecycleStatus + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public ProductSpecificationLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

}
