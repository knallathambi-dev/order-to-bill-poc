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

package com.orange.discobole.processflow.ssm.dto;

import org.springframework.statemachine.transition.Transition;

import java.util.Map;

/**
 * The class StateMachineTransition is wrapper over {@link Transition}
 *
 * @author Sunny Srivastava
 * @since 1.0
 */
public class StateMachineTransition {

    private String processInstanceId;

    private String taskDefinitionKey;

    private String taskDefinitionId;

    private String processDefinitionKey;

    private Transition<String, String> transition;

    private Map<String, Object> variablesFromUserActions;

    @Override
    public String toString() {
        return "StateMachineTransition{" +
                "processInstanceId='" + processInstanceId + '\'' +
                ", taskDefinitionKey='" + taskDefinitionKey + '\'' +
                ", taskDefinitionId='" + taskDefinitionId + '\'' +
                ", processDefinitionKey='" + processDefinitionKey + '\'' +
                ", transition=" + transition +
                ", variablesFromUserActions=" + variablesFromUserActions +
                '}';
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public String getTaskDefinitionId() {
        return taskDefinitionId;
    }

    public void setTaskDefinitionId(String taskDefinitionId) {
        this.taskDefinitionId = taskDefinitionId;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public Transition<String, String> getTransition() {
        return transition;
    }

    public void setTransition(Transition<String, String> transition) {
        this.transition = transition;
    }

    public Map<String, Object> getVariablesFromUserActions() {
        return variablesFromUserActions;
    }

    public void setVariablesFromUserActions(Map<String, Object> variablesFromUserActions) {
        this.variablesFromUserActions = variablesFromUserActions;
    }
}