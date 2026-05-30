// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import java.util.List;
import java.util.Map;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;

/**
 * The Class InvalidProductOfferingRelatedPartyEvent stores invalid related
 * parties entered by user.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class InvalidProductOfferingRelatedPartyEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final List<RelatedParty> invalidRelatedParties;

	private InvalidProductOfferingRelatedPartyEvent() {
		this.productOfferingId = null;
		this.invalidRelatedParties = null;
	}

	public InvalidProductOfferingRelatedPartyEvent(String productOfferingId,
			List<RelatedParty> invalidRelatedParties) {
		this.productOfferingId = productOfferingId;
		this.invalidRelatedParties = invalidRelatedParties;
	}

	@Override
	public String toString() {
		return "InvalidProductOfferingRelatedPartyEvent [productOfferingId=" + productOfferingId
				+ ", invalidRelatedParties=" + invalidRelatedParties + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public  List<RelatedParty> getInvalidRelatedParties() {
		return invalidRelatedParties;
	}

}
