// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;

/**
 * The Class AtomicProductOfferingCreationCompletedEvent shows the completed
 * product offering created.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class AtomicProductOfferingCreationCompletedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final ProductOffering productOffering;


	public AtomicProductOfferingCreationCompletedEvent() {
		this.productOffering = null;
		this.productOfferingId = null;
	}

	public AtomicProductOfferingCreationCompletedEvent(String productOfferingId,ProductOffering productOffering) {
		this.productOfferingId = productOfferingId;
		this.productOffering = productOffering;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingCreationCompletedEvent [productOffering=" + productOffering + "]";
	}

	public ProductOffering getProductOffering() {
		return productOffering;
	}
	public String getProductOfferingId() {
		return productOfferingId;
	}


}
