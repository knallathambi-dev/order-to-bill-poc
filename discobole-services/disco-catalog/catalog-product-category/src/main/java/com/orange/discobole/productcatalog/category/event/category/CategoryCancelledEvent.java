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
 * CategoryCancelledEvent represents cancellation of category .
 * 
 * @author BMKJ8547
 *
 */
public class CategoryCancelledEvent implements CategoryEvent {
	@TargetAggregateIdentifier
	private final Category category;
	private final String categoryId;
	private final OffsetDateTime lastUpdate;

	public CategoryCancelledEvent() {
		super();
		this.category = null;
		this.categoryId = null;
		this.lastUpdate = null;
	}

	public CategoryCancelledEvent(Category category, String categoryId, OffsetDateTime lastUpdate) {
		super();
		this.category = category;
		this.categoryId = categoryId;
		this.lastUpdate = lastUpdate;
	}

	public Category getCategory() {
		return category;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "CategoryCancelledEvent [category=" + category + ", categoryId=" + categoryId + ", lastUpdate="
				+ lastUpdate + "]";
	}

}
