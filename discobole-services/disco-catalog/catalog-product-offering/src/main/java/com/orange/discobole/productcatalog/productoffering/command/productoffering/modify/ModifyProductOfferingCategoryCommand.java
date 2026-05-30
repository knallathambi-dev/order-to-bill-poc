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

public class ModifyProductOfferingCategoryCommand {
	@TargetAggregateIdentifier
	private String productOfferingId;
	private final List<String> categories;
	private boolean isAtomic;

	/**
	 * @param categories
	 */
	public ModifyProductOfferingCategoryCommand(String productOfferingId,List<String> categories ,boolean isAtomic) {
		this.productOfferingId =  productOfferingId;
		this.categories = categories;
		this.isAtomic = isAtomic;
	}




	@Override
	public String toString() {
		return "ModifyProductOfferingCategoryCommand [productOfferingId=" + productOfferingId + "categories=" + categories + 
				"isAtomic"+ isAtomic + "]";
	}

	public List<String> getCategories() {
		return categories;
	}
	
	public String getProductOfferingId() {
		return productOfferingId;
	}
	
	public boolean getIsAtomic() {
		return isAtomic;
	}

	

	
}
