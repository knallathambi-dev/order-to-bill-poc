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

import com.orange.discobole.productcatalog.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;

public class ModifyAssociatePOPtoOperationSpecificationCommand {
	
	@TargetAggregateIdentifier
    private final String productOfferingId;
	private final List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList;

	/**
	 * @param associatePOPtoOperationSpecList
	 */
	public ModifyAssociatePOPtoOperationSpecificationCommand(String productOfferingId,
			List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList) {
		this.productOfferingId = productOfferingId;
		this.associatePOPtoOperationSpecList = associatePOPtoOperationSpecList;
	}

	@Override
	public String toString() {
		return "ModifyAssociatePOPtoOperationSpecificationCommand [associatePOPtoOperationSpecList="
				+ associatePOPtoOperationSpecList + "]";
	}

	public List<AssociatePOPtoOperationSpec> getAssociatePOPtoOperationSpecList() {
		return associatePOPtoOperationSpecList;
	}
	

	public String getProductOfferingId() {
		return productOfferingId;
	}


}
