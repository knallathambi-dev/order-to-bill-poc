// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;

/**
 * The Class BundleProductOfferingTermDefinedEvent defines bundle product
 * offering term using valid for entered and verified.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class BundleProductOfferingTermDefinedEvent implements BundleProductOfferingEvent {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<ProductOfferingTerm> productOfferingTerm;
	private final OffsetDateTime lastUpdate;

	/**
	 * @param productOfferingId
	 * @param productOfferingTerm
	 * @param lastUpdate
	 */
	public BundleProductOfferingTermDefinedEvent(String productOfferingId,
			List<ProductOfferingTerm> productOfferingTerm, OffsetDateTime lastUpdate) {
		super();
		this.productOfferingId = productOfferingId;
		this.productOfferingTerm = productOfferingTerm;
		this.lastUpdate = lastUpdate;
	}

	public BundleProductOfferingTermDefinedEvent() {
		super();
		this.productOfferingId = null;
		this.productOfferingTerm = null;
		this.lastUpdate = null;
	}

	@Override
	public String toString() {
		return "BundleProductOfferingTermDefinedEvent [productOfferingId=" + productOfferingId
				+ ", productOfferingTerm=" + productOfferingTerm + ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}


	public List<ProductOfferingTerm> getProductOfferingTerm() {
		return productOfferingTerm;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
