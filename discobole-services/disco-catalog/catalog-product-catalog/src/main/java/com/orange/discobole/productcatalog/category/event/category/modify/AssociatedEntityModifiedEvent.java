// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.category.modify;

import java.time.OffsetDateTime;
import java.util.Set;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.event.category.CategoryEvent;

public class AssociatedEntityModifiedEvent implements CategoryEvent {
	private final String productOfferingId;
	private final String categoryId;
	private final Set<ProductOfferingRef> addProductOfferings;
	private final Set<ProductOfferingRef> delProductOfferings;
	private final OffsetDateTime lastUpdate;
	public AssociatedEntityModifiedEvent(String productOfferingId,String categoryId, Set<ProductOfferingRef> addProductOfferings,
			Set<ProductOfferingRef> delProductOfferings, OffsetDateTime lastUpdate) {
		super();
		this.productOfferingId = productOfferingId;
		this.categoryId = categoryId;
		this.addProductOfferings = addProductOfferings;
		this.delProductOfferings = delProductOfferings;
		this.lastUpdate = lastUpdate;
	}
	public AssociatedEntityModifiedEvent() {
		super();
		this.productOfferingId = null;
		this.categoryId = null;
		this.addProductOfferings = null;
		this.delProductOfferings = null;
		this.lastUpdate = null;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public Set<ProductOfferingRef> getAddProductOfferings() {
		return addProductOfferings;
	}
	public Set<ProductOfferingRef> getDelProductOfferings() {
		return delProductOfferings;
	}
	public String getProductOfferingId() {
		return productOfferingId;
	}
	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}
	@Override
	public String toString() {
		return "ModifyCategoryAssociateEntityEvent [categoryId=" + categoryId + ", addProductOfferings="
				+ addProductOfferings + ", delProductOfferings=" + delProductOfferings + ", lastUpdate=" + lastUpdate
				+ ", productOfferingId=" + productOfferingId +"]";
	}
	
}
