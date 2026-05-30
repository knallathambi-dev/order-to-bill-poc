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

import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.LifecycleState;

/**
 * UpdateLifeCycleSCommand is used to upadte the lifecycle of the selected
 * entity.
 * 
 * @author Vivek Singh
 *
 */
public class UpdateLifeCycleCommand {
	@TargetAggregateIdentifier
	private final String aggregateId;
	private final LifecycleState state;
	private final String version;

	
	public UpdateLifeCycleCommand(String aggregateId, LifecycleState state,String version) {
		super();
		this.aggregateId = aggregateId;
		this.state = state;
		this.version= version;
	}

	public UpdateLifeCycleCommand() {
		super();
		this.aggregateId = null;
		this.state = null;
		this.version = null;
	}

	public String getAggregateId() {
		return aggregateId;
	}


	public LifecycleState getState() {
		return state;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "UpdateLifeCycleCommand{" +
				"aggregateId='" + aggregateId + '\'' +
				", state=" + state +
				", version='" + version + '\'' +
				'}';
	}
}
