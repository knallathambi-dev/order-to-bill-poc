// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class AtomicProductOfferingModifiedValidatedCommand {
   
	@TargetAggregateIdentifier
	private String productOffId;

	/**
	 * @param productOffId
	 */
	public AtomicProductOfferingModifiedValidatedCommand(String productOffId) {
		super();
		this.productOffId = productOffId;
	}

	public String getProductOffId() {
		return productOffId;
	}

	public void setProductOffId(String productOffId) {
		this.productOffId = productOffId;
	}


	@Override
	public String toString() {
		return "AtomicProductOfferingModifiedValidatedCommand [productOffId=" + productOffId + "]";
	}
	

}
