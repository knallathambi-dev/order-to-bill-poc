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
 * @author Varshika Choudhary
 */
public class SelectCategoryDeleteCommand {
	@TargetAggregateIdentifier
    private final String categoryId;
	private final String categoryType;

    /**
     * @param categoryId
     */
    public SelectCategoryDeleteCommand(String categoryId,String categoryType) {
        this.categoryId = categoryId;
		this.categoryType = categoryType;
    }

    @Override
    public String toString() {
        return "SelectCategoryDeleteCommand [categoryId=" + categoryId +", categoryType="+categoryType+"]";
    }

    public String getcategoryId() {
        return categoryId;
    }

	public String getCategoryType() {
		return categoryType;
	}

}
