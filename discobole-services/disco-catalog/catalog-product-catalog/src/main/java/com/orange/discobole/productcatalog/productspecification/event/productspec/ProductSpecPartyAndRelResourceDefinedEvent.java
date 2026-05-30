// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.time.OffsetDateTime;
import java.util.List;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.RelatedResource;

/**
 * Event that will be raised when product specification raleted party and
 * resource is valid and get configured.
 * 
 * @author Vivek Singh
 * @since 1.0
 *
 */

public class ProductSpecPartyAndRelResourceDefinedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final List<RelatedParty> relatedParties;
	private final List<RelatedResource> realtedResources;
	private final OffsetDateTime lastUpdate;

	private ProductSpecPartyAndRelResourceDefinedEvent() {
		productSpecId = null;
		relatedParties = null;
		realtedResources = null;
		lastUpdate = null;
	}

	public ProductSpecPartyAndRelResourceDefinedEvent(String productSpecId, List<RelatedParty> relatedParties,
			List<RelatedResource> realtedResources, OffsetDateTime lastUpdate) {

		this.productSpecId = productSpecId;
		this.relatedParties = relatedParties;
		this.realtedResources = realtedResources;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ProductSpecPartyAndRelResourceDefinedEvent [productSpecId=" + productSpecId + ", relatedParties="
				+ relatedParties + ", realtedResources=" + realtedResources + ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public List<RelatedParty> getRelatedParties() {
		return relatedParties;
	}

	public List<RelatedResource> getRealtedResources() {
		return realtedResources;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
