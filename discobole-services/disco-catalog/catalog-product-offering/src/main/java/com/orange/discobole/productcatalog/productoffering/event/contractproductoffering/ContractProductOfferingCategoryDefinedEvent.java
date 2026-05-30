// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.contractproductoffering;

import java.time.OffsetDateTime;
import java.util.Set;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.CategoryRef;

/**
 * The Class BundleProductOfferingCategoryDefinedEvent.
 *
 * @author Ayush Khanna
 * @since 1.0
 */
public class ContractProductOfferingCategoryDefinedEvent implements ContractProductOfferingEvent {
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final Set<CategoryRef> categories;
	private final OffsetDateTime lastUpdate;
	
	public ContractProductOfferingCategoryDefinedEvent() {
		super();
		this.productOfferingId = null;
		this.categories = null;
		this.lastUpdate = null;
	}

	/**
	 * @param productOfferingId
	 * @param categories
	 * @param lastUpdate
	 */
	public ContractProductOfferingCategoryDefinedEvent(String productOfferingId, Set<CategoryRef> categories,
			OffsetDateTime lastUpdate) {
		super();
		this.productOfferingId = productOfferingId;
		this.categories = categories;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "BundleProductOfferingCategoryDefinedEvent [productOfferingId=" + productOfferingId + ", categories="
				+ categories + ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}


	public Set<CategoryRef> getCategories() {
		return categories;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
