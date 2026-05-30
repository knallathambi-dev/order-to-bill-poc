// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationLifecycle;

/**
 * Event raised after Product Spec state is verified
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public final class ProductSpecStateVerifiedEvent implements ProductOfferingEvent {

	@TargetAggregateIdentifier
	private final String productofferingId;
	private final String productSpecId;
	private final ProductSpecificationLifecycle resourceState;

	private ProductSpecStateVerifiedEvent() {
		this.productofferingId = null;
		this.productSpecId = null;
		this.resourceState = null;
	
	}

	public ProductSpecStateVerifiedEvent(String productofferingId,String productSpecId, ProductSpecificationLifecycle resourceState) {
		this.productSpecId = productSpecId;
		this.resourceState = resourceState;
		this.productofferingId = productofferingId;
		
	}

	@Override
	public String toString() {
		return "ProductSpecStateVerifiedEvent{" + "productSpecId='" + productSpecId + '\'' + ", resourceState="
				+ resourceState + '\''+ "productofferingId" + productofferingId +'}';
	}

	public String getProductSpecId() {
		return productSpecId;
	}
	
	public String getProductofferingId() {
		return productofferingId;
	}

	public ProductSpecificationLifecycle getResourceState() {
		return resourceState;
	}


}
