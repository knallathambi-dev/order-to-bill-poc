// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering;

/**
 * Event raised if Bundle Product Offerings is Empty.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class InvalidBundleProductOfferingsEvent implements BundleProductOfferingEvent {

	private final String productOfferingId;

	/**
	 * @param productOfferingId
	 */
	public InvalidBundleProductOfferingsEvent(String productOfferingId) {
		this.productOfferingId = productOfferingId;
	}

	public InvalidBundleProductOfferingsEvent() {
		this.productOfferingId = null;
	}

	@Override
	public String toString() {
		return "InvalidBundleProductOfferingsEvent [productOfferingId=" + productOfferingId + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

}
