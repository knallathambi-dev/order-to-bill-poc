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

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.event.category.CategoryEvent;

public class CategoryModificationValidatedEvent implements CategoryEvent {
	
	@TargetAggregateIdentifier
	private final String categoryId;
	private final Category category;
	private final OffsetDateTime lastUpdate;
	public CategoryModificationValidatedEvent(String categoryId, Category category, OffsetDateTime lastUpdate) {
		super();
		this.categoryId = categoryId;
		this.category = category;
		this.lastUpdate = lastUpdate;
	}
	public CategoryModificationValidatedEvent() {
		super();
		this.categoryId = null;
		this.category = null;
		this.lastUpdate = null;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public Category getCategory() {
		return category;
	}
	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}
	@Override
	public String toString() {
		return "CategoryModificationValidatedEvent [categoryId=" + categoryId + ", category=" + category
				+ ", lastUpdate=" + lastUpdate + "]";
	}
	
}
