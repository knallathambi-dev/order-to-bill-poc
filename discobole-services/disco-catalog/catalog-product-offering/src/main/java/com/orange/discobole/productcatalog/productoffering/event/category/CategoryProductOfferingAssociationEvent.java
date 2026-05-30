// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.category;

import java.util.Set;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingRef;

public class CategoryProductOfferingAssociationEvent implements CategoryProductOfferingEvent{
	
	@TargetAggregateIdentifier
	private final String categoryId;
	private final Set<ProductOfferingRef> productOfferings;
	private final Boolean isAddition;
	public CategoryProductOfferingAssociationEvent(String categoryId, Set<ProductOfferingRef> productOfferings,
			Boolean isAddition) {
		super();
		this.categoryId = categoryId;
		this.productOfferings = productOfferings;
		this.isAddition = isAddition;
	}
	
	public CategoryProductOfferingAssociationEvent() {
		super();
		this.categoryId = null;
		this.productOfferings = null;
		this.isAddition = null;
	}

	public String getCategoryId() {
		return categoryId;
	}
	public Set<ProductOfferingRef> getProductOfferings() {
		return productOfferings;
	}
	public Boolean getIsAddition() {
		return isAddition;
	}
	@Override
	public String toString() {
		return "CategoryProductOfferingAssociationEvent [categoryId=" + categoryId + ", productOfferings="
				+ productOfferings + ", isAddition=" + isAddition + "]";
	}
	
	
}
