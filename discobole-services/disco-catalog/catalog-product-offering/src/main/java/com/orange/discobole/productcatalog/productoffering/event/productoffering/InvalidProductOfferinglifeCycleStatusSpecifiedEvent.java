// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;

/**
 * Event class which represent the invalid lifecycle status define for product
 * offering id.
 * 
 * @author Vishal Vachaspati
 * @since 1.0
 *
 */
public class InvalidProductOfferinglifeCycleStatusSpecifiedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final ProductOfferingLifecycle lifeCycle;

	public InvalidProductOfferinglifeCycleStatusSpecifiedEvent(){
		this.productOfferingId = null;
		this.lifeCycle = null;
	}

	/**
	 * @param productOfferingId
	 * @param lifeCycle
	 */
	public InvalidProductOfferinglifeCycleStatusSpecifiedEvent(String productOfferingId,
			ProductOfferingLifecycle lifeCycle) {
		this.productOfferingId = productOfferingId;
		this.lifeCycle = lifeCycle;
	}

	@Override
	public String toString() {
		return "InvalidProductOfferinglifeCycleStatusSpecifiedEvent [productOfferingId=" + productOfferingId
				+ ", lifeCycle=" + lifeCycle + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public ProductOfferingLifecycle getLifeCycle() {
		return lifeCycle;
	}

}
