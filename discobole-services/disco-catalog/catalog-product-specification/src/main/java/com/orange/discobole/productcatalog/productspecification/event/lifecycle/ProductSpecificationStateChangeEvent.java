// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.lifecycle;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;

/**
 * The ProductSpecificationStateChangeEvent event shows the changed state of
 * product specification.
 *
 * @author Vivek Singh
 *
 */
public class ProductSpecificationStateChangeEvent implements LifeCycleEvent {

	private final String productSpecId;
	private final ProductSpecificationLifecycle currentState;
	private final ProductSpecificationLifecycle nextState;

	private ProductSpecificationStateChangeEvent() {
		productSpecId = null;
		currentState = null;
		nextState = null;
	}

	public ProductSpecificationStateChangeEvent(String productSpecId, ProductSpecificationLifecycle currentState,
			ProductSpecificationLifecycle nextState) {
		this.productSpecId = productSpecId;
		this.currentState = currentState;
		this.nextState = nextState;
	}

	@Override
	public String toString() {
		return "ProductSpecificationStateChangeEvent [productSpecId=" + productSpecId + ", currentState=" + currentState
				+ ", nextState=" + nextState + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public ProductSpecificationLifecycle getCurrentState() {
		return currentState;
	}

	public ProductSpecificationLifecycle getNextState() {
		return nextState;
	}

}
