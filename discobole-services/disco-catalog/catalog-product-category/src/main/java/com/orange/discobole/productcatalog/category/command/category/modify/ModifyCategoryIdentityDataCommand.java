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

/**
 * ModifyCategoryIdentityDataCommand is to modify the category identity data.
 * 
 * @author BMKJ8547
 *
 */
public class ModifyCategoryIdentityDataCommand {
	@TargetAggregateIdentifier
	private final String categoryId;
	private final String name;
	private final String description;
	private final Boolean isRoot;
	private final String parentId;
	private final List<String> subCategoryIds;

	private final List<String> productOfferingIds;
	
	public ModifyCategoryIdentityDataCommand(String categoryId, String name, String description, Boolean isRoot,
											 String parentId, List<String> subCategoryIds, List<String> productOfferingIds) {
		super();
		this.categoryId = categoryId;
		this.name = name;
		this.description = description;
		this.isRoot = isRoot;
		this.parentId = parentId;
		this.subCategoryIds = subCategoryIds;
		this.productOfferingIds = productOfferingIds;
	}
	
	public List<String> getSubCategoryIds() {
		return subCategoryIds;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public Boolean getIsRoot() {
		return isRoot;
	}
	public String getParentId() {
		return parentId;
	}
	public List<String> getProductOfferingIds() {
		return productOfferingIds;
	}

	@Override
	public String toString() {
		return "ModifyCategoryIdentityDataCommand{" +
				"categoryId='" + categoryId + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", isRoot=" + isRoot +
				", parentId='" + parentId + '\'' +
				", subCategoryIds=" + subCategoryIds +
				", productOfferingIds=" + productOfferingIds +
				'}';
	}


}
