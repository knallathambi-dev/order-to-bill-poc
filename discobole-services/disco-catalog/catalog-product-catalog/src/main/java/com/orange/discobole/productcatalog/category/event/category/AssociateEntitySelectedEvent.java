// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.category;

import java.time.OffsetDateTime;
import java.util.Set;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingRef;

public class AssociateEntitySelectedEvent implements CategoryEvent {
	private final String categoryId;
	private final Set<ProductOfferingRef> productOfferings;
	private final OffsetDateTime lastUpdate;

	public AssociateEntitySelectedEvent(String categoryId, Set<ProductOfferingRef> productOfferings,
			OffsetDateTime lastUpdate) {
		super();
		this.categoryId = categoryId;
		this.productOfferings = productOfferings;
		this.lastUpdate = lastUpdate;
	}

	public AssociateEntitySelectedEvent() {
		super();
		this.categoryId = null;
		this.productOfferings = null;
		this.lastUpdate = null;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public Set<ProductOfferingRef> getProductOfferings() {
		return productOfferings;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "AssociateEntitySelectedEvent [categoryId=" + categoryId + ", productOfferings=" + productOfferings
				+ ", lastUpdate=" + lastUpdate + "]";
	}


}