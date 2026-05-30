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

public class ModifyProductOfferingMarketCommand {
	
	@TargetAggregateIdentifier
	private final String productOfferingId;

	private List<String> marketSegments;

	/**
	 * @param marketSegments
	 */
	public ModifyProductOfferingMarketCommand(String productOfferingId,List<String> marketSegments) {
		super();
		this.productOfferingId = productOfferingId;
		this.marketSegments = marketSegments;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingMarketCommand [marketSegments=" + marketSegments + "]";
	}

	public List<String> getMarketSegments() {
		return marketSegments;
	}

	
	

}
