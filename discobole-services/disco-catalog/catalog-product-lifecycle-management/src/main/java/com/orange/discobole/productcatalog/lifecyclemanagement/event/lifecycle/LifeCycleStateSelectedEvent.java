// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle;

import java.time.OffsetDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;

/**
 * The LifeCycleStateSelectedEvent is used to define the valid lifecycle change
 * event.
 * 
 * @author Vivek Singh
 *
 */
public class LifeCycleStateSelectedEvent implements LifeCycleEvent {
	@TargetAggregateIdentifier
    private String aggregateId;
	private String entityId;
	private EntityType entityType;
	private final String currentState;
	private final String oldState;
	private final OffsetDateTime lastUpdate;
	private final String version;

	private LifeCycleStateSelectedEvent() {
		this.aggregateId=null;
		this.entityId = null;
		this.entityType = null;
		this.currentState = null;
		this.oldState = null;
		this.lastUpdate = null;
		this.version = null;
	}

	public LifeCycleStateSelectedEvent(String aggregateId,String entityId, EntityType entityType, String currentState, String oldState,
			OffsetDateTime lastUpdate, String version) {
		this.aggregateId=aggregateId;
		this.entityId = entityId;
		this.entityType = entityType;
		this.currentState = currentState;
		this.oldState = oldState;
		this.lastUpdate = lastUpdate;
		this.version = version;
	}

	

	@Override
	public String toString() {
		return "LifeCycleStateSelectedEvent [aggregateId=" + aggregateId + ", entityId=" + entityId + ", entityType="
				+ entityType + ", currentState=" + currentState + ", oldState=" + oldState + ", lastUpdate="
				+ lastUpdate + "]";
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

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public String getCurrentState() {
		return currentState;
	}

	public String getOldState() {
		return oldState;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public String getVersion() {
		return version;
	}


}
