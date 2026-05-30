// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.command.category.delete;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * SelectCategoryTypeDeleteCommand is to select entity type .
 *
 * @author Varshika Choudhary
 *
 */
public class SelectCategoryTypeDeleteCommand {

	@TargetAggregateIdentifier
	private final String categoryId;
    private final String categoryType;

    public SelectCategoryTypeDeleteCommand(String categoryId,String categoryType) {
        super();
        this.categoryType = categoryType;
		this.categoryId = categoryId;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public String getCategoryId() {
		return categoryId;
	}

	@Override
    public String toString() {
        return "SelectCategoryTypeDeleteCommand [categoryId="+categoryId+", categoryType=" + categoryType + "]";
    }

}