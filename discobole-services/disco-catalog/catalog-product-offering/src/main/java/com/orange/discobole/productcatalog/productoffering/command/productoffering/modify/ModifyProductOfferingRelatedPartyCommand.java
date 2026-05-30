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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;

public class ModifyProductOfferingRelatedPartyCommand {
	@TargetAggregateIdentifier
	private final String productofferingId;
	private final List<RelatedParty> relatedParties;

	/**
	 * @param relatedParties
	 */
	public ModifyProductOfferingRelatedPartyCommand(String productofferingId,List<RelatedParty> relatedParties) {
		super();
		this.productofferingId = productofferingId;
		this.relatedParties = relatedParties;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingRelatedPartyCommand [relatedParties=" + relatedParties + "]";
	}

	public List<RelatedParty> getRelatedParties() {
		return relatedParties;
	}

	public String getProductofferingId() {
		return productofferingId;
	}
	
	

}
