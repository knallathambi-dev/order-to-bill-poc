// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering.modify;

import java.time.OffsetDateTime;
import java.util.Set;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

/**
 * Event raised to modify the PO Categories from the user.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class AtomicProductOfferingCategoryModifiedEvent implements ProductOfferingEvent {

	private final String productOfferingId;

	private final Set<CategoryRef> addCategories;
	private final Set<CategoryRef> delCategories;
	private final OffsetDateTime lastUpdate;
	
	/**
	 * @param productOfferingId
	 * @param addCategories
	 * @param delCategories
	 * @param lastUpdate
	 */
	
	public AtomicProductOfferingCategoryModifiedEvent(String productOfferingId, Set<CategoryRef> addCategories,
			Set<CategoryRef> delCategories, OffsetDateTime lastUpdate) {
		super();
		this.productOfferingId = productOfferingId;
		this.addCategories = addCategories;
		this.delCategories = delCategories;
		this.lastUpdate = lastUpdate;
	}

	public AtomicProductOfferingCategoryModifiedEvent() {
		super();
		this.productOfferingId = null;
		this.addCategories = null;
		this.delCategories = null;
		this.lastUpdate = null;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public Set<CategoryRef> getAddCategories() {
		return addCategories;
	}

	public Set<CategoryRef> getDelCategories() {
		return delCategories;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingCategoryModifiedEvent [productOfferingId=" + productOfferingId + ", addCategories="
				+ addCategories + ", delCategories=" + delCategories + ", lastUpdate=" + lastUpdate + "]";
	}
	
	
}
