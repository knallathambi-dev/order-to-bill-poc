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
import java.util.Map;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;

/**
 * The Class ProductOfferingRelatedPartyCommand to store the id and roles
 * entered by user.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class ProductOfferingRelatedPartyCommand {
	
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<RelatedParty> relatedParties;


	public ProductOfferingRelatedPartyCommand(String productOfferingId,List<RelatedParty> relatedParties) {
		this.productOfferingId = productOfferingId;
		this.relatedParties = relatedParties;
	}

	@Override
	public String toString() {
		return "ProductOfferingRelatedPartyCommand [relatedParties=" + relatedParties + "productOfferingId=" + productOfferingId + "]";
	}


	public List<RelatedParty> getRelatedParties() {
		return relatedParties;
	}
	
	public String getProductOfferingId() {
		return productOfferingId;
	}
}
