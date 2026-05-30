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

public final class DefineProductOfferingCategoryCommand {
	
	@TargetAggregateIdentifier
	private final String productOfferingId;
    private final List<String> categories;

    public DefineProductOfferingCategoryCommand(String productOfferingId,List<String> categories) {
        this.categories = categories;
        this.productOfferingId = productOfferingId;
        
    }

    @Override
    public String toString() {
        return "DefineProductOfferingCategoryCommand{" +
                "categories=" + categories +
                "productOfferingId=" + productOfferingId +
                '}';
    }

    public List<String> getCategories() {
        return categories;
    }

	public String getproductOfferingId() {
		return productOfferingId;
	}

}
