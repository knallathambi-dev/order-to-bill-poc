// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

/**
 * Event raised after selection of Product Spec
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public final class ProductSpecSelectedEvent implements ProductOfferingEvent {
     
	 
	private final String productSpecId;




	private ProductSpecSelectedEvent() {
		this.productSpecId = null;
		
	}
	
	

	public ProductSpecSelectedEvent(String productSpecId) {
		this.productSpecId = productSpecId;

	}
	

	@Override
	public String toString() {
		return "ProductSpecSelectedEvent{" + "productSpecId='" + productSpecId + '\'' + '}';
	}

	public String getProductSpecId() {
		return productSpecId;
	}

}
