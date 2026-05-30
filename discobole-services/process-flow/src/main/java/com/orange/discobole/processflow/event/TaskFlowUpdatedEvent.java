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
import com.orange.discobole.processflow.dto.generated.Characteristic;

/**
 * The Class TaskFlowUpdatedEvent is raised during completion of next user task
 * triggered by UpdateTaskCommand.
 *
 * @author Sunny Srivastava
 * @since 1.0
 */
public final class TaskFlowUpdatedEvent implements Event {
	@TargetAggregateIdentifier
	private final String processFlowId;
    private final DiscoTaskFlow discoTaskFlow;

    private final Map<String, Object> variablesFromUserActions;

    private final List<Characteristic> taskCharacteristicList;

    private final Map<String, Object> processTaskIds;

    private final Map<String, Object> variables;

    public TaskFlowUpdatedEvent() {
        this.processFlowId = null;
        this.discoTaskFlow = null;
        this.variablesFromUserActions = null;
        this.taskCharacteristicList = null;
        this.processTaskIds = null;
        this.variables = null;
    }

    public TaskFlowUpdatedEvent(String processFlowId, DiscoTaskFlow discoTaskFlow, Map<String, Object> variablesFromUserActions, List<Characteristic> taskCharacteristicList, Map<String, Object> processTaskIds, Map<String, Object> variables) {
        this.processFlowId = processFlowId;
        this.discoTaskFlow = discoTaskFlow;
        this.variablesFromUserActions = variablesFromUserActions;
        this.taskCharacteristicList = taskCharacteristicList;
        this.processTaskIds = processTaskIds;
        this.variables = variables;
    }

    @Override
    public String toString() {
        return "TaskFlowUpdatedEvent{" +
                "processFlowId='" + processFlowId + '\'' +
                ", discoTaskFlow=" + discoTaskFlow +
                ", variablesFromUserActions=" + variablesFromUserActions +
                ", taskCharacteristicList=" + taskCharacteristicList +
                ", processTaskIds=" + processTaskIds +
                ", variables=" + variables +
                '}';
    }

    public String getProcessFlowId() {
        return processFlowId;
    }

    public DiscoTaskFlow getDiscoTaskFlow() {
        return discoTaskFlow;
    }

    public Map<String, Object> getVariablesFromUserActions() {
        return variablesFromUserActions;
    }

    public List<Characteristic> getTaskCharacteristicList() {
        return taskCharacteristicList;
    }

    public Map<String, Object> getProcessTaskIds() {
        return processTaskIds;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }
}
