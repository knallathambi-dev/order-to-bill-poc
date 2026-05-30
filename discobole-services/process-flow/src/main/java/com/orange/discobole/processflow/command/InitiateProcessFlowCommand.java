// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;

public final class InitiateProcessFlowCommand {
	@TargetAggregateIdentifier
	private final String processFlowId;
    private final ProcessFlowCreate processFlowCreate;


    public InitiateProcessFlowCommand(String processFlowId, ProcessFlowCreate processFlowCreate) {
		super();
		this.processFlowId = processFlowId;
		this.processFlowCreate = processFlowCreate;
	}


	public String getProcessFlowId() {
		return processFlowId;
	}


	public ProcessFlowCreate getProcessFlowCreate() {
		return processFlowCreate;
	}


	@Override
	public String toString() {
		return "InitiateProcessFlowCommand [processFlowId=" + processFlowId + ", processFlowCreate=" + processFlowCreate
				+ "]";
	}

	
}
