// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.EntityType;

public class LifeCycleEntitySelectEvent implements LifeCycleEvent {
	private String aggregateId;
	private final String entityId;
	private final EntityType entityType;

	private LifeCycleEntitySelectEvent() {
		this.aggregateId=null;
		this.entityId = null;
		this.entityType = null;
	}

	public LifeCycleEntitySelectEvent(String aggregateId,String entityId, EntityType entityType) {
		this.aggregateId=aggregateId;
		this.entityId = entityId;
		this.entityType = entityType;
	}

	
	@Override
	public String toString() {
		return "LifeCycleEntitySelectEvent [aggregateId=" + aggregateId + ", entityId=" + entityId + ", entityType="
				+ entityType + "]";
	}
	
	public String getAggregateId() {
		return aggregateId;
	}

	public void setAggregateId(String aggregateId) {
		this.aggregateId = aggregateId;
	}

	public String getEntityId() {
		return entityId;
	}

	public EntityType getEntityType() {
		return entityType;
	}

}
