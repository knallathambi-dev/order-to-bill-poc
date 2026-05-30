// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.productoffering;

import java.time.OffsetDateTime;
import java.util.Set;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef;

public class ProductOfferingCategoryAssociationDeletedEvent implements ProductOfferingCategoryEvent{
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final Set<CategoryRef> delCategories;
	private final Boolean isAddition;
	public ProductOfferingCategoryAssociationDeletedEvent(String productOfferingId, Set<CategoryRef> delCategories,
			Boolean isAddition) {
		super();
		this.productOfferingId = productOfferingId;
		this.delCategories = delCategories;
		this.isAddition = isAddition;
	}
	public ProductOfferingCategoryAssociationDeletedEvent() {
		super();
		this.productOfferingId = null;
		this.delCategories = null;
		this.isAddition = null;
	}
	public String getProductOfferingId() {
		return productOfferingId;
	}
	public Set<CategoryRef> getDelCategories() {
		return delCategories;
	}
	public Boolean getIsAddition() {
		return isAddition;
	}
	@Override
	public String toString() {
		return "ProductOfferingCategoryAssociationDeletedEvent [productOfferingId=" + productOfferingId
				+ ", delCategories=" + delCategories + ", isAddition=" + isAddition + "]";
	}
	
	 
}
