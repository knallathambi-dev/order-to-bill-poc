// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;
public class ProductSpecStatusVerifiedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private final ProductSpecificationLifecycle lifecycleStatus;

	private ProductSpecStatusVerifiedEvent() {
		productSpecId = null;
		lifecycleStatus = null;
	}

	public ProductSpecStatusVerifiedEvent(String productSpecId, ProductSpecificationLifecycle lifecycleStatus) {
		this.productSpecId = productSpecId;
		this.lifecycleStatus = lifecycleStatus;
	}

	@Override
	public String toString() {
		return "ProductSpecStatusVerifiedEvent [productSpecId=" + productSpecId + ", lifecycleStatus=" + lifecycleStatus
				+ "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public ProductSpecificationLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

}
