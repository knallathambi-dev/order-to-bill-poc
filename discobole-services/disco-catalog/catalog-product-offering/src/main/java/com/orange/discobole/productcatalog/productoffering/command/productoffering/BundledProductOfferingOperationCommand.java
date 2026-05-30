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

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;

public class BundledProductOfferingOperationCommand {
	
	@TargetAggregateIdentifier
    private final String productOfferingId;


	private final List<CommercialOperation> bundledproductOffOperationSpecification;

	/**
	 * @param bundledproductOffOperationSpecification
	 */
	public BundledProductOfferingOperationCommand(String productOfferingId,List<CommercialOperation> bundledproductOffOperationSpecification) {
		this.productOfferingId = productOfferingId;
		this.bundledproductOffOperationSpecification = bundledproductOffOperationSpecification;
	}

	@Override
	public String toString() {
		return "BundledProductOfferingOperationCommand [bundledproductOffOperationSpecification="
				+ bundledproductOffOperationSpecification + "]";
	}

	public List<CommercialOperation> getBundledproductOffOperationSpecification() {
		return bundledproductOffOperationSpecification;
	}
	
	public String getProductOfferingId() {
		return productOfferingId;
	}

}
