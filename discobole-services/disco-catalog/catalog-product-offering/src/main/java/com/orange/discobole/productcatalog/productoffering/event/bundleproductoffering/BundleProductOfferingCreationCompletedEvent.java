// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;

/**
 * The Class BundleProductOfferingCreationCompletedEvent shows the completed bundle
 * product offering created.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class BundleProductOfferingCreationCompletedEvent implements BundleProductOfferingEvent {

	private final ProductOffering productOffering;

	public BundleProductOfferingCreationCompletedEvent() {
		this.productOffering = null;
	}
	
	/**
	 * @param productOffering
	 */
	public BundleProductOfferingCreationCompletedEvent(ProductOffering productOffering) {
		this.productOffering = productOffering;
	}

	@Override
	public String toString() {
		return "BundleProductOfferingCreationCompletedEvent [productOffering=" + productOffering + "]";
	}

	public ProductOffering getProductOffering() {
		return productOffering;
	}

}
