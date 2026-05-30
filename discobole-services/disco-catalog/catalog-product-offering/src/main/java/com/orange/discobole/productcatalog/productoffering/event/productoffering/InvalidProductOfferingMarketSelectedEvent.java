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

/**
 * The Class InvalidProductOfferingMarketSelectedEvent.
 *
 * @author Diksha Srivatava
 * @since 1.0
 */
public class InvalidProductOfferingMarketSelectedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final List<String> invalidMarkets;

	private InvalidProductOfferingMarketSelectedEvent() {
		this.productOfferingId = null;
		this.invalidMarkets = null;
	}

	public InvalidProductOfferingMarketSelectedEvent(String productOfferingId, List<String> invalidMarkets) {
		this.productOfferingId = productOfferingId;
		this.invalidMarkets = invalidMarkets;
	}

	@Override
	public String toString() {
		return "InvalidProductOfferingMarketSelectedEvent [productOfferingId=" + productOfferingId + ", invalidMarkets="
				+ invalidMarkets + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public List<String> getInvalidMarkets() {
		return invalidMarkets;
	}

}
