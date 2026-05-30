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
 * This class represents an invalid event and that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.productoffering.ProductOffCancelCommand}
 * is triggered.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public class InvalidProductOffCancelledEvent implements ProductOfferingEvent {

	private final String productOffId;
	private final ProductOfferingLifecycle currentLifeCycle;
	private final ProductOfferingLifecycle lifeCycleShould;

	private InvalidProductOffCancelledEvent() {
		this.productOffId = null;
		this.currentLifeCycle = null;
		this.lifeCycleShould = null;
	}

	public InvalidProductOffCancelledEvent(String productOffId, ProductOfferingLifecycle currentLifeCycle,
			ProductOfferingLifecycle lifeCycleShould) {
		this.productOffId = productOffId;
		this.currentLifeCycle = currentLifeCycle;
		this.lifeCycleShould = lifeCycleShould;
	}

	@Override
	public String toString() {
		return "InvalidProductOffCancelledEvent{" + "productOffId='" + productOffId + '\'' + ", currentLifeCycle="
				+ currentLifeCycle + ", lifeCycleShould=" + lifeCycleShould + '}';
	}

	public String getProductOffId() {
		return productOffId;
	}

	public ProductOfferingLifecycle getCurrentLifeCycle() {
		return currentLifeCycle;
	}

	public ProductOfferingLifecycle getLifeCycleShould() {
		return lifeCycleShould;
	}
}
