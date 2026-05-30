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

public class ProductOfferingInternalProjectionEvent implements ProductOfferingEvent{
	private final String productOfferingId;
    private final List<String> categoryId;

	public ProductOfferingInternalProjectionEvent(){
		this.productOfferingId = null;
		this.categoryId = null;
	}

	public ProductOfferingInternalProjectionEvent(String productOfferingId, List<String> categoryId) {
		super();
		this.productOfferingId = productOfferingId;
		this.categoryId = categoryId;
	}
	public String getProductOfferingId() {
		return productOfferingId;
	}
	public List<String> getCategoryId() {
		return categoryId;
	}
	@Override
	public String toString() {
		return "ProductOfferingInternalProjectionEvent [productOfferingId=" + productOfferingId + ", CategoryId="
				+ categoryId + "]";
	}
    
    
}
