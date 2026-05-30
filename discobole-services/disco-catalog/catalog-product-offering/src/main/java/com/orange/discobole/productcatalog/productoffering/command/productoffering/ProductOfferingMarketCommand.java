// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * The Class ProductOfferingMarketCommand.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public class ProductOfferingMarketCommand {
	
	@TargetAggregateIdentifier
    private final String productOfferingId;
	private final List<String> marketSegments;

	public ProductOfferingMarketCommand(String productOfferingId,List<String> marketSegments) {
		this.productOfferingId = productOfferingId;
		this.marketSegments = marketSegments;
	}

	@Override
	public String toString() {
		return "ProductOfferingMarketCommand [marketSegments=" + marketSegments + "productOfferingId=" +productOfferingId +"]";
	}

	public List<String> getMarketSegments() {
		return marketSegments;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

}
