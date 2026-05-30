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

package com.orange.discobole.processflow.aggregate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.ObjectStateMachineFactory;
import org.springframework.statemachine.config.model.StateMachineModel;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.TaskFlowStateType;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.dto.generated.TaskLink;
import com.orange.discobole.processflow.resolver.StateMachineNextTransitionResolver;
import com.orange.discobole.processflow.ssm.builder.StateMachineModelBuilder;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.processflow.util.AutomateState;

import jakarta.annotation.Resource;

@Service
public class StateMachineUtil {
	
	@Resource
	private ApplicationContext appCtx;
	@Resource
	private StateMachineNextTransitionResolver<String,String> stateMachineNextTransitionResolver;
	
	/**
	 * Update ProcessFLow when task is updated
	 *
	 * @param taskFlow
	 * @return ProcessFlow
	 */
	private static final Logger LOGGER = LogManager.getLogger(StateMachineUtil.class);

	public TaskLink getTaskLink(StateMachineTransition task, Map<Object, Object> contextVariables) {
		final TaskLink nextTask = new TaskLink();
		nextTask.setTitle(task.getProcessDefinitionKey() + '.' + task.getTaskDefinitionKey());
		nextTask.state(TaskFlowStateType.ACTIVE);
		nextTask.setTaskFlowSpecificationId(task.getTaskDefinitionId());
		if (appCtx.containsBean(nextTask.getTitle())) {
			nextTask.setTaskFlowSpecificationCharacteristic(
					appCtx.getBean(nextTask.getTitle(), UserAction.class).requiredCharacteristics(task,contextVariables));
		}
		return nextTask;
	}

	/**
	 * Start process instance by key.
	 *
	 * @param processFlowId
	 * @param processDefinitionKey the process definition key
	 * @param variables            the variables
	 * @return the state machine
	 */
	public StateMachine<String, String> startProcessInstanceByKey(String processFlowId,
			final String processDefinitionKey, final Map<String, Object> variables) {
		appCtx.containsBean("acvb");
		StateMachineModel<String, String> stateMachineModel = appCtx.getBean(StateMachineModelBuilder.class)
				.build(processDefinitionKey);
		ObjectStateMachineFactory<String, String> factory = getFactory(stateMachineModel);
		final UUID statemachineUUID = processFlowId != null ? UUID.fromString(processFlowId) : UUID.randomUUID();
		StateMachine<String, String> sm = factory.getStateMachine(statemachineUUID);
		sm.start();
		sm.getExtendedState().getVariables().putAll(variables);
		return sm;
	}
	public Map<String, Boolean> setConfigurationData(String processDefinitionKey) {
		return appCtx.getBean(StateMachineModelBuilder.class).getEditableStates(processDefinitionKey);
	}

	public Map<String, Boolean> setConfigurationDataHiddenStates(String processDefinitionKey) {
		return appCtx.getBean(StateMachineModelBuilder.class).getHiddenStates(processDefinitionKey);
	}

	public ObjectStateMachineFactory<String, String> getFactory(
			final StateMachineModel<String, String> stateMachineModel) {
		return new ObjectStateMachineFactory<>(stateMachineModel);
	}
	/**
	 * Gets the available transition to initiate.
	 *
	 * @param processFlowInstanceId the process flow instance id
	 * @return the available transition to initiate
	 */
	public List<StateMachineTransition> getAvailableTransitionToInitiate(final String processFlowInstanceId,
			final StateMachine<String, String> stateMachine,Map<String, Object> 
	variablesFromUserActions,Map<String, Object> processTaskIds) {
		List<Transition<String, String>> transitionList = stateMachineNextTransitionResolver
				.getAvailableTask(stateMachine);
		
		List<StateMachineTransition> stateMachineTransitions = new ArrayList<>();
		transitionList.forEach(transition -> {
			final StateMachineTransition stateMachineTransition = new StateMachineTransition();

			stateMachineTransition.setProcessDefinitionKey(stateMachine.getId());
			stateMachineTransition.setProcessInstanceId(processFlowInstanceId);
			if (transition.getSource().getId().equals("INNERREGION"))
				stateMachineTransition.setTaskDefinitionKey(transition.getTarget().getId());
			else
				stateMachineTransition.setTaskDefinitionKey(transition.getSource().getId());
			
			stateMachineTransition.setTransition(transition);
			stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);
			String taskId="";
			if (transition.getSource().getId().equals("INNERREGION")) {
				taskId = stateMachine.getExtendedState().getVariables()
						.get(processFlowInstanceId + '.' + transition.getTarget().getId()) != null
										? String.valueOf(stateMachine.getExtendedState().getVariables()
										.get(processFlowInstanceId + '.' + transition.getTarget().getId()))									
								: UUID.randomUUID().toString();	
										stateMachineTransition.setTaskDefinitionId(taskId);
										stateMachine.getExtendedState().getVariables()
												.put(processFlowInstanceId + '.' + transition.getTarget().getId(), taskId);
										processTaskIds.put(processFlowInstanceId + '.' + transition.getTarget().getId(), taskId);
										stateMachineTransitions.add(stateMachineTransition);
			}
				
			else {
				taskId = stateMachine.getExtendedState().getVariables()
						.get(processFlowInstanceId + '.' + transition.getSource().getId()) != null
										? String.valueOf(stateMachine.getExtendedState().getVariables()
										.get(processFlowInstanceId + '.' + transition.getSource().getId()))
												
												
								: UUID.randomUUID().toString();	
										stateMachineTransition.setTaskDefinitionId(taskId);
										stateMachine.getExtendedState().getVariables()
												.put(processFlowInstanceId + '.' + transition.getSource().getId(), taskId);
										processTaskIds.put(processFlowInstanceId + '.' + transition.getSource().getId(), taskId);
										stateMachineTransitions.add(stateMachineTransition);
			}
			
		});
		return stateMachineTransitions;
	}

	public Map<String, Object> performEditableTask(StateMachineTransition stateMachineTransition,
			TaskFlowUpdate taskFlowUpdate) {
		Map<String, Object> variablesFromTask = new HashMap<>();

		if (appCtx.containsBean(stateMachineTransition.getProcessDefinitionKey() + '.'
				+ stateMachineTransition.getTaskDefinitionKey())) {
			final UserAction userAction = appCtx.getBean(stateMachineTransition.getProcessDefinitionKey() + '.'
					+ stateMachineTransition.getTaskDefinitionKey(), UserAction.class);
			variablesFromTask = userAction.perform(stateMachineTransition, taskFlowUpdate);
		}
		return variablesFromTask;

	}
	public Map<String, Object> performUserTask(StateMachineTransition transition,TaskFlowUpdate taskFlowUpdate,StateMachine<String, String> stateMachine) {
		Map<String, Object> variablesFromTask = new HashMap<>();
		if (appCtx.containsBean(transition.getProcessDefinitionKey() + '.' + transition.getTaskDefinitionKey())) {
			final UserAction userAction = appCtx.getBean(
					transition.getProcessDefinitionKey() + '.' + transition.getTaskDefinitionKey(),
					UserAction.class);
			variablesFromTask = userAction.perform(transition, taskFlowUpdate);

			if (variablesFromTask.containsKey("StopStateMachine")) {
				stateMachine.getTransitions().clear();
				stateMachine.stop();

			}

		}
		return variablesFromTask;
	}

	public void runAutomateState(StateMachine<String, String> stateMachine) {
		String[] automateStatesBeans = appCtx.getBeanNamesForType(AutomateState.class);
		for (String automateState : automateStatesBeans) {
			appCtx.getBean(automateState, AutomateState.class).automateState(stateMachine);
		}
	}
}
