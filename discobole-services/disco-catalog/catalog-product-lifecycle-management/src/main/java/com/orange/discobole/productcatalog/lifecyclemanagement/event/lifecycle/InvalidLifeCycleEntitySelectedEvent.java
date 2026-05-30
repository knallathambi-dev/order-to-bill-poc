// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;

/**
 * The InvalidLifeCycleEntitySelectEvent represent the entity id and type which
 * is not valid.
 * 
 * @author Vivek Singh
 *
 */
public class InvalidLifeCycleEntitySelectedEvent implements Event {
	@TargetAggregateIdentifier
	private String aggregateId;
	private String entityId;
	private EntityType entityType;

	

	public InvalidLifeCycleEntitySelectedEvent() {
		super();
		this.aggregateId = null;
		this.entityId = null;
		this.entityType = null;
	}


	public InvalidLifeCycleEntitySelectedEvent(String aggregateId, String entityId, EntityType entityType) {
		super();
		this.aggregateId = aggregateId;
		this.entityId = entityId;
		this.entityType = entityType;
	}

	public String getAggregateId() {
		return aggregateId;
	}


	public String getEntityId() {
		return entityId;
	}



	public EntityType getEntityType() {
		return entityType;
	}

	@Override
	public String toString() {
		return "InvalidLifeCycleEntitySelectedEvent [aggregateId=" + aggregateId + ", entityId=" + entityId
				+ ", entityType=" + entityType + "]";
	}

	

}
