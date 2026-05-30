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

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.category.dto.generated.common.Category;

/**
 * CategoryCreationEvent represents category creation .
 * 
 * @author BMKJ8547
 *
 */
public class CategoryCreationEvent implements CategoryEvent {
	@TargetAggregateIdentifier
	private final String categoryId;
	private final Category category;
	private final OffsetDateTime lastUpdate;

	public CategoryCreationEvent(String categoryId, Category category, OffsetDateTime lastUpdate) {
		super();
		this.categoryId = categoryId;
		this.category = category;
		this.lastUpdate = lastUpdate;
	}

	public CategoryCreationEvent() {
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
		return "CategoryCreationEvent [categoryId=" + categoryId + ", category=" + category + ", lastUpdate="
				+ lastUpdate + "]";
	}

}
