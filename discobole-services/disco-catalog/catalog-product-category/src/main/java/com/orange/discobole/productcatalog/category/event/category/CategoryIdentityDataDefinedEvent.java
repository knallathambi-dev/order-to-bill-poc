// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.category;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef;

/**
 * CategoryIdentityDataDefinedEvent represents identity data defined for
 * category .
 * 
 * @author BMKJ8547
 *
 */
public class CategoryIdentityDataDefinedEvent implements CategoryEvent {
	@TargetAggregateIdentifier
	private final String categoryId;
	private final String name;
	private final String description;
	private final Boolean isRoot;
	private final String parentId;
	private final OffsetDateTime lastUpdate;
	private final List<CategoryRef> subCategories;
	private final Set<ProductOfferingRef> productOfferings;
	private final String href;

	public CategoryIdentityDataDefinedEvent(String categoryId, String name, String description, Boolean isRoot,
											String parentId, OffsetDateTime lastUpdate, List<CategoryRef> subCategories, Set<ProductOfferingRef> productOfferings, String href) {
		super();
		this.categoryId = categoryId;
		this.name = name;
		this.description = description;
		this.isRoot = isRoot;
		this.parentId = parentId;
		this.lastUpdate = lastUpdate;
		this.subCategories = subCategories;
		this.productOfferings = productOfferings;
		this.href = href;
	}

	public CategoryIdentityDataDefinedEvent() {
		super();
		this.categoryId = null;
		this.name = null;
		this.description = null;
		this.parentId = null;
		this.isRoot = null;
		this.lastUpdate = null;
		this.subCategories = null;
		this.productOfferings = null;
		this.href = null;
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

	public List<CategoryRef> getSubCategories() {
		return subCategories;
	}

	public String getHref() {
		return href;
	}
	public Set<ProductOfferingRef> getProductOfferings() {
		return productOfferings;
	}

	@Override
	public String toString() {
		return "CategoryIdentityDataDefinedEvent{" +
				"categoryId='" + categoryId + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", isRoot=" + isRoot +
				", parentId='" + parentId + '\'' +
				", lastUpdate=" + lastUpdate +
				", subCategories=" + subCategories +
				", productOfferings=" + productOfferings +
				", href='" + href + '\'' +
				'}';
	}
}
