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

package com.orange.discobole.processflow.event;

import java.util.List;
import java.util.Map;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.processflow.dto.DiscoTaskFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;

public final class ProcessFlowCreatedEvent implements Event {
	@TargetAggregateIdentifier
	private final String processFlowId;
	private final ProcessFlow processFlow;
	private final List<DiscoTaskFlow> discoTaskFlows;
	private final Map<String, Object> processTaskIds;
	private final Map<String, Object> variables;

	public ProcessFlowCreatedEvent() {
		processFlowId = null;
		processFlow = null;
		discoTaskFlows = null;
		processTaskIds = null;
		variables = null;
	}

	public ProcessFlowCreatedEvent(String processFlowId, ProcessFlow processFlow, List<DiscoTaskFlow> discoTaskFlows,
			Map<String, Object> processTaskIds, Map<String, Object> variables) {
		this.processFlowId = processFlowId;
		this.processFlow = processFlow;
		this.discoTaskFlows = discoTaskFlows;
		this.processTaskIds = processTaskIds;
		this.variables = variables;
	}

	@Override
	public String toString() {
		return "ProcessFlowCreatedEvent{" + "processFlowId='" + processFlowId + '\'' + ", processFlow=" + processFlow
				+ ", discoTaskFlows=" + discoTaskFlows + ", processTaskIds=" + processTaskIds + ", variables=" + variables + '}';
	}

	public String getProcessFlowId() {
		return processFlowId;
	}

	public ProcessFlow getProcessFlow() {
		return processFlow;
	}

	public List<DiscoTaskFlow> getDiscoTaskFlows() {
		return discoTaskFlows;
	}

	public Map<String, Object> getProcessTaskIds() {
		return processTaskIds;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}
}
