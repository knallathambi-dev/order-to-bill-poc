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

/**
 * The Class ContractProductOfferingVersionCreatedEvent stores the created version
 * of product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class ContractProductOfferingVersionCreatedEvent implements ContractProductOfferingEvent {

	private final String productOfferingId;
	private final String productOfferingVersion;
	private final OffsetDateTime lastUpdate;

	
	public ContractProductOfferingVersionCreatedEvent() {
		this.productOfferingId = null;
		this.productOfferingVersion = null;
		this.lastUpdate = null;
	}
	
	/**
	 * @param productOfferingId
	 * @param productOfferingVersion
	 * @param lastUpdate
	 */
	public ContractProductOfferingVersionCreatedEvent(String productOfferingId, String productOfferingVersion,
			OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.productOfferingVersion = productOfferingVersion;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ContractProductOfferingVersionCreatedEvent [productOfferingId=" + productOfferingId
				+ ", productOfferingVersion=" + productOfferingVersion + ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public String getProductOfferingVersion() {
		return productOfferingVersion;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
