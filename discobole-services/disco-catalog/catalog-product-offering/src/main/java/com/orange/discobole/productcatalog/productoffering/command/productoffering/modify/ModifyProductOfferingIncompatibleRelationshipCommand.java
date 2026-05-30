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

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;

public class ModifyProductOfferingIncompatibleRelationshipCommand {
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final String addIncompatibleProductOfferingsRelationship;
	private final Boolean idAdd;
	private final ProductOfferingType productOfferingType;
	

	public ModifyProductOfferingIncompatibleRelationshipCommand(String productOfferingId,
			String addIncompatibleProductOfferingsRelationship, Boolean idAdd, ProductOfferingType productOfferingType) {
		this.productOfferingId = productOfferingId;
		this.addIncompatibleProductOfferingsRelationship = addIncompatibleProductOfferingsRelationship;
		this.idAdd = idAdd;
		this.productOfferingType = productOfferingType;
		
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public String getAddIncompatibleProductOfferingsRelationship() {
		return addIncompatibleProductOfferingsRelationship;
	}

	public Boolean getIdAdd() {
		return idAdd;
	}

	public ProductOfferingType getProductOfferingType() {
		return productOfferingType;
	}


	@Override
	public String toString() {
		return "ModifyProductOfferingIncompatibleRelationshipCommand [productOfferingId=" + productOfferingId
				+ ", addIncompatibleProductOfferingsRelationship=" + addIncompatibleProductOfferingsRelationship
				+ ", idAdd=" + idAdd + ", productOfferingType=" + productOfferingType + 
				   "]";
	}

}
