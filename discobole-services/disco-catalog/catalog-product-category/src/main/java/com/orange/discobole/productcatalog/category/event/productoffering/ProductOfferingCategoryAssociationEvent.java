// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.productoffering;

import java.util.Set;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef;

public class ProductOfferingCategoryAssociationEvent implements ProductOfferingCategoryEvent {


	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final Set<CategoryRef> categories;
	private final Boolean isAddition;

	private final  String productOfferingType;

	public ProductOfferingCategoryAssociationEvent(String productOfferingId, Set<com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef> categories, Boolean isAddition, String productOfferingType) {
		this.productOfferingId = productOfferingId;
		this.categories = categories;
		this.isAddition = isAddition;
		this.productOfferingType = productOfferingType;
	}

	public ProductOfferingCategoryAssociationEvent() {
		super();
		this.productOfferingId = null;
		this.categories = null;
		this.isAddition = null;
		this.productOfferingType=null;
	}
	public String getProductOfferingId() {
		return productOfferingId;
	}
	public Set<CategoryRef> getCategories() {
		return categories;
	}

	public Boolean isIsAddition() {
		return isAddition;
	}

	public String getProductOfferingType() {
		return productOfferingType;
	}

	@Override
	public String toString() {
		return "ProductOfferingCategoryAssociationEvent{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", categories=" + categories +
				", isAddition=" + isAddition +
				", productOfferingType=" + productOfferingType +
				'}';
	}

}
