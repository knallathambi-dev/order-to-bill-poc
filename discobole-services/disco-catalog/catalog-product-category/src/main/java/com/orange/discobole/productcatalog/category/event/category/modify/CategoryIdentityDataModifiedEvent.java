// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.category.modify;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.category.event.category.CategoryEvent;

public class CategoryIdentityDataModifiedEvent implements CategoryEvent {
	
	@TargetAggregateIdentifier
	private final String categoryId;
	private final String name;
	private final String description;
	private final Boolean isRoot;
	private final String parentId;
	private final OffsetDateTime lastUpdate;
	private final List<CategoryRef> subCategories;

	private final Set<ProductOfferingRef> productOfferings;

	public CategoryIdentityDataModifiedEvent(String categoryId, String name, String description, Boolean isRoot, String parentId, OffsetDateTime lastUpdate, List<CategoryRef> subCategories, Set<ProductOfferingRef> productOfferings) {
		this.categoryId = categoryId;
		this.name = name;
		this.description = description;
		this.isRoot = isRoot;
		this.parentId = parentId;
		this.lastUpdate = lastUpdate;
		this.subCategories = subCategories;
		this.productOfferings = productOfferings;
	}

	public CategoryIdentityDataModifiedEvent() {
		super();
		this.categoryId = null;
		this.name = null;
		this.description = null;
		this.isRoot = null;
		this.parentId = null;
		this.lastUpdate = null;
		this.subCategories = null;
		this.productOfferings = null;
	}

	public Set<ProductOfferingRef> getProductOfferings() {
		return productOfferings;
	}

	public List<CategoryRef> getSubCategories() {
		return subCategories;
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
	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "CategoryIdentityDataModifiedEvent{" +
				"categoryId='" + categoryId + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", isRoot=" + isRoot +
				", parentId='" + parentId + '\'' +
				", lastUpdate=" + lastUpdate +
				", subCategories=" + subCategories +
				", productOfferings=" + productOfferings +
				'}';
	}
}
