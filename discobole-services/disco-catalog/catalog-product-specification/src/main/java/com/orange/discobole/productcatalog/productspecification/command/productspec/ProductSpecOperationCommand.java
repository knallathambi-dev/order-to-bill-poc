// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.OperationSpecification;

public class ProductSpecOperationCommand {
	
	@TargetAggregateIdentifier
	private String productSpecId;
	private final List<OperationSpecification> operations;

	public ProductSpecOperationCommand(String productSpecId,List<OperationSpecification> operations) {
		this.operations = operations;
		this.productSpecId = productSpecId;
	}

	@Override
	public String toString() {
		return "ProductSpecOperationCommand{" +"productSpecId="+productSpecId+ ", operations=" + operations + '}';
	}

	public List<OperationSpecification> getOperations() {
		return operations;
	}

	/**
	 * @return the productSpecId
	 */
	public String getProductSpecId() {
		return productSpecId;
	}

	
}
