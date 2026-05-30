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

import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryLifeCycle;

/**
 * EntityTypeSelectedEvent represents entityType of category .
 * 
 * @author BMKJ8547
 *
 */
public class EntityTypeSelectedEvent implements CategoryEvent {
	@TargetAggregateIdentifier
	private final String categoryId;
	private final String categoryType;
	private final OffsetDateTime lastUpdate;

	public EntityTypeSelectedEvent() {
		super();
		this.categoryId = null;
		this.categoryType = null;
		this.lastUpdate = null;
	}

	public EntityTypeSelectedEvent(String categoryId, String categoryType, OffsetDateTime lastUpdate) {
		super();
		this.categoryId = categoryId;
		this.categoryType = categoryType;
		this.lastUpdate = lastUpdate;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "EntityTypeSelectedEvent [categoryId=" + categoryId + ", categoryTtype=" + categoryType + ", lastUpdate="
				+ lastUpdate + "]";
	}

}
