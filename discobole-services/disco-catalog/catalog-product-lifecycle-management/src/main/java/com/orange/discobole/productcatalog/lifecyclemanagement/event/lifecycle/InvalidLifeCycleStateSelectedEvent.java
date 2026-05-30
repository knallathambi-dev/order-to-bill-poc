// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle;

import java.util.Set;

import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;

/**
 * The InvalidLifeCycleStateSelectedEvent is used to represent the invalid next
 * state selected for particular entity.
 * 
 * @author Ankur Singh
 *
 */
public class InvalidLifeCycleStateSelectedEvent implements LifeCycleEvent {
	private String aggregateId;
	private String entityId;
	private EntityType entityType;
	private final String currentState;
	private final Set<String> nextPossibleStates;

	private InvalidLifeCycleStateSelectedEvent() {
		this.aggregateId=null;
		this.entityId = null;
		this.entityType = null;
		this.currentState = null;
		this.nextPossibleStates = null;
	}

	public InvalidLifeCycleStateSelectedEvent(String aggregateId,String entityId, EntityType entityType, String currentState,
			Set<String> nextPossibleStates) {
		this.aggregateId=aggregateId;
		this.entityId = entityId;
		this.entityType = entityType;
		this.currentState = currentState;
		this.nextPossibleStates = nextPossibleStates;
	}

	@Override
	public String toString() {
		return "InvalidLifeCycleStateSelectedEvent [aggregateId=" + aggregateId + ", entityId=" + entityId
				+ ", entityType=" + entityType + ", currentState=" + currentState + ", nextPossibleStates="
				+ nextPossibleStates + "]";
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

	public Set<String> getNextPossibleStates() {
		return nextPossibleStates;
	}

}
