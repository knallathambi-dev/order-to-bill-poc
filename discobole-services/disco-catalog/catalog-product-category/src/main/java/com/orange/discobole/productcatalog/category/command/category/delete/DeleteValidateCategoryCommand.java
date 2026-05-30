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

public class DeleteValidateCategoryCommand {
	@TargetAggregateIdentifier
	private final String categoryId;

	public DeleteValidateCategoryCommand(String categoryId) {
		super();
		this.categoryId = categoryId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	@Override
	public String toString() {
		return "DeleteValidateCategoryCommand [categoryId=" + categoryId + "]";
	}
	
}
