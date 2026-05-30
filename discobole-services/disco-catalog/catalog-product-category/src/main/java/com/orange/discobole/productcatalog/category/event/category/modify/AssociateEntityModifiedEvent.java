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

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.event.category.CategoryEvent;

public class AssociateEntityModifiedEvent implements CategoryEvent {
	
	@TargetAggregateIdentifier
	private final String categoryId;
	private final Set<ProductOfferingRef> addProductOfferings;
	private final Set<ProductOfferingRef> deleteProductOfferings;
	private final OffsetDateTime lastUpdate;
	public AssociateEntityModifiedEvent(String categoryId, Set<ProductOfferingRef> addProductOfferings,
			Set<ProductOfferingRef> deleteProductOfferings, OffsetDateTime lastUpdate) {
		super();
		this.categoryId = categoryId;
		this.addProductOfferings = addProductOfferings;
		this.deleteProductOfferings = deleteProductOfferings;
		this.lastUpdate = lastUpdate;
	}
	
	public AssociateEntityModifiedEvent() {
		super();
		this.categoryId = null;
		this.addProductOfferings = null;
		this.deleteProductOfferings = null;
		this.lastUpdate = null;
	}
	
	public String getCategoryId() {
		return categoryId;
	}

	public Set<ProductOfferingRef> getAddProductOfferings() {
		return addProductOfferings;
	}

	public Set<ProductOfferingRef> getDeleteProductOfferings() {
		return deleteProductOfferings;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "AssociateEntityModifiedEvent [categoryId=" + categoryId + ", addProductOfferings=" + addProductOfferings
				+ ", deleteProductOfferings=" + deleteProductOfferings + ", lastUpdate=" + lastUpdate + "]";
	}
	
}
