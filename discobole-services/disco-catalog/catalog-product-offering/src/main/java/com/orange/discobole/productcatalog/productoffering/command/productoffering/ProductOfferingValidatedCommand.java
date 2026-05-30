// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * The Class ProductOfferingValidatedCommand validates the product
 * offering and sets its version.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class ProductOfferingValidatedCommand {
	
	@TargetAggregateIdentifier
	private final String productOfferingId;
	
	

	public ProductOfferingValidatedCommand(String productOfferingId) {
		super();
		this.productOfferingId = productOfferingId;
	}



	public String getProductOfferingId() {
		return productOfferingId;
	}
	
	
	
	

}
