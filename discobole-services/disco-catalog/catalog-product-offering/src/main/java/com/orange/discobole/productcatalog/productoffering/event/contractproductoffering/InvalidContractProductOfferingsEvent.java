// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.contractproductoffering;

/**
 * Event raised if contract Product Offerings is Empty.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class InvalidContractProductOfferingsEvent implements ContractProductOfferingEvent {

	private final String productOfferingId;

	/**
	 * @param productOfferingId
	 */
	public InvalidContractProductOfferingsEvent(String productOfferingId) {
		this.productOfferingId = productOfferingId;
	}

	public InvalidContractProductOfferingsEvent() {
		this.productOfferingId = null;
	}

	@Override
	public String toString() {
		return "InvalidContractProductOfferingsEvent [productOfferingId=" + productOfferingId + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

}
