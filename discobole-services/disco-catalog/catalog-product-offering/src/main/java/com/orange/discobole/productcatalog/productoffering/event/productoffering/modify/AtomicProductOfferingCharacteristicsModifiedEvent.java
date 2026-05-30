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
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristicValueUse;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

/**
 * The Class AtomicProductOfferingCharacteristicsModifiedEvent stores the
 * characteristics modified for product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class AtomicProductOfferingCharacteristicsModifiedEvent implements ProductOfferingEvent {
   
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<ProductSpecificationCharacteristicValueUse> productSpecificationCharacteristicValueUse;
	private final OffsetDateTime lastUpdate;

	public AtomicProductOfferingCharacteristicsModifiedEvent(){
		this.productOfferingId = null;
		this.productSpecificationCharacteristicValueUse = null;
		this.lastUpdate = null;
	}

	/**
	 * @param productOfferingId
	 * @param productSpecificationCharacteristicValueUse
	 * @param lastUpdate
	 */
	public AtomicProductOfferingCharacteristicsModifiedEvent(String productOfferingId,
			List<ProductSpecificationCharacteristicValueUse> productSpecificationCharacteristicValueUse,
			OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.productSpecificationCharacteristicValueUse = productSpecificationCharacteristicValueUse;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingCharacteristicsModifiedEvent [productOfferingId=" + productOfferingId
				+ ", productSpecificationCharacteristicValueUse=" + productSpecificationCharacteristicValueUse
				+ ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public List<ProductSpecificationCharacteristicValueUse> getProductSpecificationCharacteristicValueUse() {
		return productSpecificationCharacteristicValueUse;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
