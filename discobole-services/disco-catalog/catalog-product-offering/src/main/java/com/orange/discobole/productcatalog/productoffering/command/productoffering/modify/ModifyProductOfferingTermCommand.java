// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;

public class ModifyProductOfferingTermCommand {
	
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<ProductOfferingTerm> productOfferingTerm;

	/**
	 * @param productOfferingTerm
	 */
	public ModifyProductOfferingTermCommand(String productOfferingId,List<ProductOfferingTerm> productOfferingTerm) {
		this.productOfferingId = productOfferingId;
		this.productOfferingTerm = productOfferingTerm;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingTermCommand [productOfferingTerm=" + productOfferingTerm + "]";
	}

	public List<ProductOfferingTerm> getProductOfferingTerm() {
		return productOfferingTerm;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}
	
	

}
