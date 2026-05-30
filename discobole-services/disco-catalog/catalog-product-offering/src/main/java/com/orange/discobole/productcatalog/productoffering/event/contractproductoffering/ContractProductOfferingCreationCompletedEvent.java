// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.contractproductoffering;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;

/**
 * The Class ContractProductOfferingCreationCompletedEvent shows the completed contract
 * product offering created.
 *
 * @author Ayush Khanna
 * @since 1.0
 */
public class ContractProductOfferingCreationCompletedEvent implements ContractProductOfferingEvent {

	private final ProductOffering productOffering;

	public ContractProductOfferingCreationCompletedEvent() {
		this.productOffering = null;
	}
	
	/**
	 * @param productOffering
	 */
	public ContractProductOfferingCreationCompletedEvent(ProductOffering productOffering) {
		this.productOffering = productOffering;
	}

	@Override
	public String toString() {
		return "ContractProductOfferingCreationCompletedEvent [productOffering=" + productOffering + "]";
	}

	public ProductOffering getProductOffering() {
		return productOffering;
	}

}
