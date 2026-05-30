// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.command.lifecycle;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * SelectProductEntityCommand is to select the entity type of which lifecycle is
 * to be update.
 * 
 * @author Vivek Singh
 *
 */
public class SelectProductEntityCommand {
	@TargetAggregateIdentifier
	private final String aggregateId;
	private final String entityId;
	private final String entityType;
	public SelectProductEntityCommand(String aggregateId, String entityId, String entityType) {
		super();
		this.aggregateId = aggregateId;
		this.entityId = entityId;
		this.entityType = entityType;
	}

	
	@Override
	public String toString() {
		return "SelectProductEntityCommand [aggregateId=" + aggregateId + ", entityId=" + entityId + ", entityType="
				+ entityType + "]";
	}


	public String getAggregateId() {
		return aggregateId;
	}


	public String getEntityId() {
		return entityId;
	}


	public String getEntityType() {
		return entityType;
	}


}
