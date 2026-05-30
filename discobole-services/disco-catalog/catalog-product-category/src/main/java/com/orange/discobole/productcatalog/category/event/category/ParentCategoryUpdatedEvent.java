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

/**
 * ParentCategoryUpdatedEvent represents parent category id of category .
 * 
 * @author BMKJ8547
 *
 */
public class ParentCategoryUpdatedEvent implements CategoryEvent {
	@TargetAggregateIdentifier
	private final String categoryId;
	private final String parentId;
	private final OffsetDateTime lastUpdate;

	public ParentCategoryUpdatedEvent(String categoryId, String parentId, OffsetDateTime lastUpdate) {
		super();
		this.categoryId = categoryId;
		this.parentId = parentId;
		this.lastUpdate = lastUpdate;
	}

	public ParentCategoryUpdatedEvent() {
		super();
		this.categoryId = null;
		this.parentId = null;
		this.lastUpdate = null;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getParentId() {
		return parentId;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "ParentCategoryUpdatedEvent [categoryId=" + categoryId + ", parentId=" + parentId + ", lastUpdate="
				+ lastUpdate + "]";
	}

}
