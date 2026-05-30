// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;


import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;

/**
 * The Class AtomicProductOfferingTermUsingValidForDefinedEvent defines product
 * offering term using valid for entered and verified.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class AtomicProductOfferingTermDefinedEvent implements ProductOfferingEvent {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<ProductOfferingTerm> productOfferingTerm;
	private final OffsetDateTime lastUpdate;

	private AtomicProductOfferingTermDefinedEvent() {
		this.productOfferingId = null;
		this.productOfferingTerm = null;
		this.lastUpdate=null;
	}

	public AtomicProductOfferingTermDefinedEvent(String productOfferingId,
			List<ProductOfferingTerm> productOfferingTerm,OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.productOfferingTerm = productOfferingTerm;
		this.lastUpdate=lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingTermDefinedEvent{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", productOfferingTerm=" + productOfferingTerm +
				", lastUpdate=" + lastUpdate +
				'}';
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
