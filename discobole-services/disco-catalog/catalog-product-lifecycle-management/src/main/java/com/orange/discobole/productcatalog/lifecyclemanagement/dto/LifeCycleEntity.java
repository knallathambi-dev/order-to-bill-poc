// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.dto;

import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;

public class LifeCycleEntity {

	private String id;
	private EntityType entityType;

	public LifeCycleEntity(String id, EntityType entityType) {
		this.id = id;
		this.entityType = entityType;
	}

	@Override
	public String toString() {
		return "LifeCycleEntity [id=" + id + ", entityType=" + entityType + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

}
