// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.command.category;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * SelectEntityTypeCommand is to select entity type .
 * 
 * @author BMKJ8547
 *
 */
public class SelectEntityTypeCommand {
	@TargetAggregateIdentifier
	private final String categoryId;
	private final String categoryType;

	public SelectEntityTypeCommand(String categoryId, String categoryType) {
		super();
		this.categoryId = categoryId;
		this.categoryType = categoryType;
	}

	@Override
	public String toString() {
		return "SelectEntityTypeCommand [categoryId=" + categoryId + ", entityType=" + categoryType + "]";
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getCategoryType() {
		return categoryType;
	}

}
