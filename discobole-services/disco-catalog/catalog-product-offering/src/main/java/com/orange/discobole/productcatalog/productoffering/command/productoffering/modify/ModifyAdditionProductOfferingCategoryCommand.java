// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import java.util.Set;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.CategoryRef;

public class ModifyAdditionProductOfferingCategoryCommand {
	@TargetAggregateIdentifier
	private String productOfferingId;
	private final Set<CategoryRef> categories;

	private String productOfferingType;

	public ModifyAdditionProductOfferingCategoryCommand(String productOfferingId, Set<CategoryRef> categories, String productOfferingType) {
		this.productOfferingId = productOfferingId;
		this.categories = categories;
		this.productOfferingType = productOfferingType;
	}

	@Override
	public String toString() {
		return "ModifyAdditionProductOfferingCategoryCommand{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", categories=" + categories +
				", productOfferingType='" + productOfferingType + '\'' +
				'}';
	}

	public String getProductOfferingType() {
		return productOfferingType;
	}

	public Set<CategoryRef> getCategories() {
		return categories;
	}
	
	public String getProductOfferingId() {
		return productOfferingId;
	}
	
}
