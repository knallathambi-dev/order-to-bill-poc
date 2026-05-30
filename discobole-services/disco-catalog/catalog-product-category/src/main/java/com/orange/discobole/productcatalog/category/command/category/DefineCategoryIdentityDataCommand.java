// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.command.category;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * DefineCategoryIdentityDataCommand is to define the category identity data.
 * 
 * @author BMKJ8547
 *
 */
public class DefineCategoryIdentityDataCommand {
	
	@TargetAggregateIdentifier
	private final String categoryId;
	private final String name;
	private final String description;	
	private final Boolean isRoot;
	private final String parentId;
	private final List<String> subCategoryIds;

	private final List<String> productOfferingIds;

	public String getHref() {
		return href;
	}

	public DefineCategoryIdentityDataCommand(String categoryId, String name, String description, Boolean isRoot, String parentId, List<String> subCategoryIds, List<String> productOfferingIds, String href) {
		this.categoryId = categoryId;
		this.name = name;
		this.description = description;
		this.isRoot = isRoot;
		this.parentId = parentId;
		this.subCategoryIds = subCategoryIds;
		this.productOfferingIds = productOfferingIds;
		this.href = href;
	}

	@Override
	public String toString() {
		return "DefineCategoryIdentityDataCommand{" +
				"categoryId='" + categoryId + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", isRoot=" + isRoot +
				", parentId='" + parentId + '\'' +
				", subCategoryIds=" + subCategoryIds +
				", productOfferingIds=" + productOfferingIds +
				", href='" + href + '\'' +
				'}';
	}

	private final String href;


	public List<String> getProductOfferingIds() {
		return productOfferingIds;
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

	public String getParentId() {
		return parentId;
	}

	public Boolean getIsRoot() {
		return isRoot;
	}

}
