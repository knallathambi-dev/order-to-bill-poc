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
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryLifeCycle;
import com.orange.discobole.productcatalog.category.event.category.CategoryEvent;

/**
 * @author BMKJ8547
 *
 */
public class CategoryModifiedInitiatedEvent implements CategoryEvent  {
	
	@TargetAggregateIdentifier
	private final String categoryId;
	private final Category category;
	private final OffsetDateTime lastUpdate;
	private final CategoryLifeCycle lifecycle;
	
	public CategoryModifiedInitiatedEvent(String categoryId, Category category, OffsetDateTime lastUpdate,
			CategoryLifeCycle lifecycle) {
		super();
		this.categoryId = categoryId;
		this.category = category;
		this.lastUpdate = lastUpdate;
		this.lifecycle = lifecycle;
	}

	public CategoryModifiedInitiatedEvent() {
		super();
		this.categoryId = null;
		this.category = null;
		this.lastUpdate = null;
		this.lifecycle = null;
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

	public CategoryLifeCycle getLifecycle() {
		return lifecycle;
	}

	@Override
	public String toString() {
		return "CategoryModifiedInitiatedEvent [categoryId=" + categoryId + ", category=" + category + ", lastUpdate="
				+ lastUpdate + ", lifecycle=" + lifecycle + "]";
	}
	
	
}
