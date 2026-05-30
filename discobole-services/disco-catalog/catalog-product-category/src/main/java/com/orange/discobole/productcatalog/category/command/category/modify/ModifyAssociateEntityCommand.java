// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.command.category.modify;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ModifyAssociateEntityCommand {
	@TargetAggregateIdentifier
	private final String categoryId;
	
	private final List<String> productOfferingIds;

	public ModifyAssociateEntityCommand(String categoryId,List<String> productOfferingIds) {
		super();
		this.categoryId = categoryId;
		this.productOfferingIds = productOfferingIds;
	}

	public String getCategoryId() {
		return categoryId;
	}

	
	public List<String> getProductOfferingIds() {
		return productOfferingIds;
	}

	@Override
	public String toString() {
		return "ModifyAssociateEntityCommand [categoryId="+categoryId + "productOfferingIds=" + productOfferingIds + "]";
	}
	
}
