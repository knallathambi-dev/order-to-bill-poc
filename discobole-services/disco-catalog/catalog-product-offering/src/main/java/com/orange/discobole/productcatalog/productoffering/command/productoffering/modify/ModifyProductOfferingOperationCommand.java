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

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;

public class ModifyProductOfferingOperationCommand {
	
	@TargetAggregateIdentifier
    private final String productOfferingId;
	private final List<CommercialOperation> productOffOperationSpecification;

	/**
	 * @param productOffOperationSpecification
	 */
	public ModifyProductOfferingOperationCommand(String productOfferingId,List<CommercialOperation> productOffOperationSpecification) {
		this.productOfferingId = productOfferingId;
		this.productOffOperationSpecification = productOffOperationSpecification;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingOperationCommand [productOffOperationSpecification="
				+ productOffOperationSpecification + "]";
	}

	public List<CommercialOperation> getProductOffOperationSpecification() {
		return productOffOperationSpecification;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}
	
	

}
