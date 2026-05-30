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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.util.CollectionUtils;

import com.orange.discobole.processflow.command.InitiateProcessFlowCommand;
import com.orange.discobole.processflow.command.UpdateTaskCommand;
import com.orange.discobole.processflow.constant.ProcessConstants;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.dto.DiscoTaskFlow;
import com.orange.discobole.processflow.dto.generated.ChannelRef;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.Links;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;
import com.orange.discobole.processflow.dto.generated.ProcessFlowStateType;
import com.orange.discobole.processflow.dto.generated.RelatedEntity;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlowStateType;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.dto.generated.TaskLink;
import com.orange.discobole.processflow.event.ProcessFlowCreatedEvent;
import com.orange.discobole.processflow.event.ProcessFlowUpdatedEvent;
import com.orange.discobole.processflow.event.TaskFlowUpdatedEvent;
import com.orange.discobole.processflow.exception.TaskFlowNotFoundException;
import com.orange.discobole.processflow.resolver.StateMachineNextTransitionResolver;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;

/**
 * The Class StateMachineAggregate handles the business logic of diferent
 * commands.
 *
 * @author Sunny Srivastava
 * @since 1.0
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Aggregate
@Component
public class StateMachineAggregate {

	private static final Logger LOGGER = LogManager.getLogger(StateMachineAggregate.class);

	@AggregateIdentifier
	private String processFlowId;
	@Resource
	private StateMachineNextTransitionResolver<String, String> stateMachineNextTransitionResolver;
	
	private StateMachineUtil stateMachineUtil;

	private Map<String, Object> variables;
	private StateMachine<String, String> stateMachine;
	private String processDefinitionKey;
	private final Map<String, Object> processTaskIds = new HashMap<>();

	private final Map<String, Object> variablesFromUserActions = new HashMap<>();

	private final List<Characteristic> taskCharacteristicList = new ArrayList<>();

	private final Set<TaskLink> editableTaskLinks = new LinkedHashSet<>();

	private final List<StateMachineTransition> stateMachineTransitionsList = new ArrayList<>();

	private final Map<String, Boolean> editableStates = new HashMap<>();

	private final Map<String, Boolean> hiddenStates = new HashMap<>();


	private final Set<String> alreadyAppliedEventIds = new HashSet<>();

	public StateMachineAggregate() {

	}

	/**
	 * Process the InitiateProcessFLowCommand which is raised while creating a new
	 * ProcessFlow.
	 *
	 * @param command the command
	 * @return the list
	 */
	@CommandHandler
	public StateMachineAggregate(final InitiateProcessFlowCommand command,StateMachineUtil stateMachineUtil) {
		this.stateMachineUtil=stateMachineUtil;
		processFlowId=command.getProcessFlowId();
		stateMachine = stateMachineUtil.startProcessInstanceByKey(processFlowId,
				command.getProcessFlowCreate().getProcessFlowSpecification(),
				setVariablesForProcessFlow(command.getProcessFlowCreate()));
		hiddenStates.putAll(stateMachineUtil.setConfigurationDataHiddenStates(this.stateMachine.getId()));
		if(stateMachine.getState().getId().equals(ProcessConstants.INITIAL_AUTOMATIC_STATE_VARIABLE)){
//			stateMachine.sendEvent(ProcessConstants.INITIAL_AUTOMATIC_EVENT_VARIABLE);
			stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(ProcessConstants.INITIAL_AUTOMATIC_EVENT_VARIABLE).build())).subscribe();
		}
		stateMachineUtil.runAutomateState(stateMachine);

		ProcessFlow processFlow = processFlowConvert(stateMachine);
		processFlow.processFlowDate(OffsetDateTime.now()).state(ProcessFlowStateType.ACTIVE).type("ProcessFlow");
		List<String> taskIds = new ArrayList<>();
		processFlow.getLinks().getNextTaskstoBePerformed()
				.forEach(nextTask -> taskIds.add(nextTask.getTaskFlowSpecificationId()));

		List<DiscoTaskFlow> discoTaskFlows = new ArrayList<>();
		taskIds.forEach(taskId -> {
			StateMachineTransition stateMachineTransition = fetchTransitionByTransitionId(processFlow.getId(), taskId);
			discoTaskFlows.add(new DiscoTaskFlow(processFlow.getId(), taskFlowConvert(stateMachineTransition)));
		});

		Map<String,Object> variables=new HashMap<>();
		variables.put(ProcessConstants.HIDDEN_STATES, hiddenStates);
		Map<Object,Object> map = stateMachine.getExtendedState().getVariables();
		for (Map.Entry<Object, Object> entry : map.entrySet()) {
			if(entry.getKey() instanceof String){
				variables.put((String) entry.getKey(), entry.getValue());
			}
		}

		processFlow.setDescription((String)this.stateMachine.getExtendedState().getVariables().get(ProcessConstants.DESCRIPTION_VARIABLE));

		AggregateLifecycle.apply(
				new ProcessFlowCreatedEvent(processFlowId, processFlow, discoTaskFlows, processTaskIds, variables));
	}

	@EventSourcingHandler
	public void on(ProcessFlowCreatedEvent event,StateMachineUtil stateMachineUtil) {
		this.stateMachineUtil=stateMachineUtil;
		processFlowId=event.getProcessFlowId();
		processDefinitionKey = event.getProcessFlow().getProcessFlowSpecification();
		variables = event.getProcessTaskIds();
		this.stateMachine=stateMachineUtil.startProcessInstanceByKey(processFlowId, processDefinitionKey, variables);
		stateMachine.getExtendedState().getVariables().put(ProcessConstants.ON_METHOD_VARIABLE,true);
		stateMachine.getExtendedState().getVariables().putAll(event.getVariables());
		if(stateMachine.getState().getId().equals(ProcessConstants.INITIAL_AUTOMATIC_STATE_VARIABLE)){
//			stateMachine.sendEvent(ProcessConstants.INITIAL_AUTOMATIC_EVENT_VARIABLE);
			stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(ProcessConstants.INITIAL_AUTOMATIC_EVENT_VARIABLE).build())).subscribe();
		}

		stateMachineUtil.runAutomateState(stateMachine);
	}

	

	/**
	 * Process the UpdateTaskCommand which is raised while completing a next task.
	 *
	 * @param command the command
	 * @return the list
	 */
	@CommandHandler
	public void updateTaskFlow(final UpdateTaskCommand command,StateMachineUtil stateMachineUtil) {
		this.stateMachineUtil=stateMachineUtil;
		this.stateMachine.getExtendedState().getVariables().remove(ProcessConstants.ON_METHOD_VARIABLE);
		this.stateMachine.getExtendedState().getVariables().remove(ProcessConstants.DESCRIPTION_VARIABLE);
		editableStates.putAll(stateMachineUtil.setConfigurationData(processDefinitionKey));
		String processFlowId = command.getProcessFlowId();
		TaskFlowUpdate taskFlowUpdate = command.getTaskFlowUpdate();
		String taskFlowId = command.getTaskFlowId();

		//In process modification if state machine is already stopped
		if (this.stateMachine.getExtendedState().getVariables().containsKey("StopStateMachine")) {
			throw new TaskFlowNotFoundException(taskFlowId);
		}

		Map<String, Object> variablesFromTask = new HashMap<>();
		StateMachineTransition transition;

		//it will execute editable tasks
		if (checkIfTaskIsFromEditableTask(taskFlowId)) {
			Optional<StateMachineTransition> transitionOptional = stateMachineTransitionsList.stream()
					.filter(stateMachineTransition -> taskFlowId.equals(stateMachineTransition.getTaskDefinitionId()))
					.findFirst();
			transition = transitionOptional.orElse(null);
			if (this.stateMachine.getTransitions().isEmpty() || transition == null) {
				throw new TaskFlowNotFoundException(taskFlowId);
			}

			variablesFromTask =stateMachineUtil.performEditableTask(transition, taskFlowUpdate);

		} else {
			transition = fetchTransitionByTransitionId(processFlowId, taskFlowId);
			if (transition == null) {
				throw new TaskFlowNotFoundException(taskFlowId);
			}
			setVariablesForTaskFLow(taskFlowUpdate);

			addTaskToListOfEditableTasks(transition);
			variablesFromTask=stateMachineUtil.performUserTask(transition, taskFlowUpdate, stateMachine);
			String event = transition.getTransition().getTrigger().getEvent();
			stateMachine.getExtendedState().getVariables().putAll(variablesFromTask);
//			stateMachine.sendEvent(event);
			stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).subscribe();
			transition.getVariablesFromUserActions().putAll(variablesFromTask);

		}
		List<Characteristic> taskCharacteristics = null != variablesFromTask.get(TaskConstants.CHARACTERISTIC)
				? (List<Characteristic>) variablesFromTask.get(TaskConstants.CHARACTERISTIC)
				: new ArrayList<>();

		TaskFlow taskFlow = taskFlowConvert(transition);
		taskFlow.state(TaskFlowStateType.ACTIVE).type("TaskFlow");
		taskFlow.setDescription((String)this.stateMachine.getExtendedState().getVariables().get(ProcessConstants.DESCRIPTION_VARIABLE));
		if (taskFlow.getLinks() != null && !taskFlow.getLinks().getNextTaskstoBePerformed().isEmpty()) {
			List<TaskLink> existingTask = new ArrayList<>(editableTaskLinks);
			taskFlow.getLinks().setExistingTaskEditable(existingTask);
		} else {
			taskFlow.getLinks().setExistingTaskEditable(new ArrayList<>());
		}

		Map<String,Object> variables=new HashMap<>();
		Map<Object,Object> map = stateMachine.getExtendedState().getVariables();
		for (Map.Entry<Object, Object> entry : map.entrySet()) {
			if(entry.getKey() instanceof String){
				variables.put((String) entry.getKey(), entry.getValue());
			}
		}


		ProcessFlow processFlow = updateProcessFlow(taskFlow);
		AggregateLifecycle.apply(new TaskFlowUpdatedEvent(command.getProcessFlowId(),new DiscoTaskFlow(processFlowId, taskFlow), variablesFromTask,
				taskCharacteristics, processTaskIds, variables));
		AggregateLifecycle.apply(new ProcessFlowUpdatedEvent(processFlow,command.getProcessFlowId()));

	}

	@EventSourcingHandler
	public void on(TaskFlowUpdatedEvent event) {
//		if (!alreadyAppliedEventIds.contains(event.getDiscoTaskFlow().getTaskFlow().getId())) {
			 this.processFlowId = event.getDiscoTaskFlow().getProcessFLowId();
			String taskFlowId = event.getDiscoTaskFlow().getTaskFlow().getId();
			editableTaskLinks.addAll(event.getDiscoTaskFlow().getTaskFlow().getLinks().getExistingTaskEditable());
			variablesFromUserActions.putAll(event.getVariablesFromUserActions());
			taskCharacteristicList.addAll(event.getTaskCharacteristicList());
			StateMachineTransition transition = fetchTransitionByTransitionId(processFlowId, taskFlowId);
			if(null!=transition) {
//			alreadyAppliedEventIds.add(transition.getTaskDefinitionId());
			stateMachineTransitionsList.addAll(List.of(transition));
			String transitionEvent = transition.getTransition().getTrigger().getEvent();
			stateMachine.getExtendedState().getVariables().putAll(event.getProcessTaskIds());
			stateMachine.getExtendedState().getVariables().putAll(event.getVariablesFromUserActions());
			stateMachine.getExtendedState().getVariables().putAll(event.getVariables());
			stateMachine.getExtendedState().getVariables().put(ProcessConstants.ON_METHOD_VARIABLE,true);
//			stateMachine.sendEvent(transitionEvent);
			stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(transitionEvent).build())).subscribe();
			}
//		}

	}
	@EventSourcingHandler
	public void on(ProcessFlowUpdatedEvent event) {
		this.processFlowId=event.getProcessFlowId();
	}

	/**
	 * Sets the variables for task Flow.
	 *
	 * @param taskFlowUpdate the new variables for task Flow
	 */
	public void setVariablesForTaskFLow(final TaskFlowUpdate taskFlowUpdate) {
		Map<String, Object> taskVariables = new HashMap<>();
		List<Characteristic> taskCharacteristicList = !this.taskCharacteristicList.isEmpty()
				? this.taskCharacteristicList
				: new ArrayList<>();
		if (null != taskFlowUpdate.getCharacteristic()) {
			taskCharacteristicList.addAll(taskFlowUpdate.getCharacteristic());
		}
		taskVariables.put(TaskConstants.CHARACTERISTIC,
				taskFlowUpdate.getCharacteristic() != null ? taskFlowUpdate.getCharacteristic() : new ArrayList<>());

		taskVariables.put(TaskConstants.CHANNEL,
				taskFlowUpdate.getChannel() != null ? taskFlowUpdate.getChannel() : new ArrayList<>());
		taskVariables.put(TaskConstants.RELATED_PARTY,
				taskFlowUpdate.getRelatedParty() != null ? taskFlowUpdate.getRelatedParty() : new ArrayList<>());
		taskVariables.put(TaskConstants.RELATED_ENTITY,
				taskFlowUpdate.getRelatedEntity() != null ? taskFlowUpdate.getRelatedEntity() : new ArrayList<>());
		stateMachine.getExtendedState().getVariables().putAll(taskVariables);
	}
	/**
	 * Sets the variables for process flow.
	 *
	 * @param processFlowCreate the process flow create
	 * @return the map
	 */
	public Map<String, Object> setVariablesForProcessFlow(final ProcessFlowCreate processFlowCreate) {
		Map<String, Object> variables = new HashMap<>();
		variables.put(ProcessConstants.CHARACTERISTIC,
				processFlowCreate.getCharacteristic() != null ? processFlowCreate.getCharacteristic()
						: new ArrayList<>());
		variables.put(ProcessConstants.CHANNEL,
				processFlowCreate.getChannel() != null ? processFlowCreate.getChannel() : new ArrayList<>());
		variables.put(ProcessConstants.RELATED_PARTY,
				processFlowCreate.getRelatedParty() != null ? processFlowCreate.getRelatedParty() : new ArrayList<>());
		variables.put(ProcessConstants.RELATED_ENTITY,
				processFlowCreate.getRelatedEntity() != null ? processFlowCreate.getRelatedEntity()
						: new ArrayList<>());
		return variables;

	}
	

	public boolean checkIfTaskIsFromEditableTask(String taskFlowId) {

		return editableTaskLinks.stream()
				.anyMatch(taskLink -> taskFlowId.equals(taskLink.getTaskFlowSpecificationId()));

	}

	public void addTaskToListOfEditableTasks(StateMachineTransition transition) {

		if (editableStates.size() > 0 && editableStates.containsKey(transition.getTaskDefinitionKey())
				&& editableStates.get(transition.getTaskDefinitionKey())) {
			final TaskLink nextTask =stateMachineUtil.getTaskLink(transition,stateMachine.getExtendedState().getVariables());
			editableTaskLinks.addAll(List.of(nextTask));
		}
	}
	/**
	 * Convert from variables.
	 *
	 * @param target the target
	 */
	@SuppressWarnings("unchecked")
	public void convertFromVariablesForTaskFlow(final TaskFlow target) {
		Map<Object, Object> valueMap = stateMachine.getExtendedState().getVariables();

		try {
			valueMap.forEach((key, value) -> {
				if (value == null) {
					return;
				}
				LOGGER.debug("key - {}\nvalue - {}", key, value);
				switch (key.toString()) {
				case TaskConstants.CHARACTERISTIC:
					final List<Characteristic> characteristicValue = (List<Characteristic>) value;
					if (target.getCharacteristic() == null) {
						target.setCharacteristic(characteristicValue);
					} else {
						target.getCharacteristic().addAll(characteristicValue);
					}
					break;
				case TaskConstants.CHANNEL:
					final List<ChannelRef> channelValue = (List<ChannelRef>) value;
					target.setChannel(channelValue);
					break;
				case TaskConstants.RELATED_PARTY:
					final List<RelatedParty> relatedPartyValue = (List<RelatedParty>) value;
					target.setRelatedParty(relatedPartyValue);
					break;
				case TaskConstants.RELATED_ENTITY:
					final List<RelatedEntity> relatedEntityValue = (List<RelatedEntity>) value;
					target.setRelatedEntity(relatedEntityValue);
					break;
				default:
					LOGGER.warn("No variable fetched");
				}
			});
		} catch (NullPointerException e) {
			LOGGER.warn("values cannot be set to target", e);
		}

	}
	/**
	 * Fetch transition by transition id.
	 *
	 * @param processFlowId the process flow id
	 * @param taskFlowId    the task flow id
	 * @return the state machine transition
	 */
	public StateMachineTransition fetchTransitionByTransitionId(final String processFlowId, final String taskFlowId) {
		List<StateMachineTransition> stateMachineTransitions = stateMachineUtil.getAvailableTransitionToInitiate(processFlowId,stateMachine,variablesFromUserActions, processTaskIds);
		return stateMachineTransitions.stream()
				.filter(stateMachineTransition1 -> taskFlowId.equals(stateMachineTransition1.getTaskDefinitionId()))
				.findFirst().orElse(null);
	}
	/**
	 * Creates the next tasks.
	 *
	 * @param processFlowInstanceId the process flow instance id
	 * @return the list
	 */
	public List<TaskLink> createNextTasks(final String processFlowInstanceId) {

		List<StateMachineTransition> taskList = stateMachineUtil.getAvailableTransitionToInitiate(processFlowId,stateMachine,variablesFromUserActions,processTaskIds);
		final Set<TaskLink> nextTasks = new LinkedHashSet<>();
		taskList.forEach(task -> {
			final TaskLink nextTask =stateMachineUtil.getTaskLink(task,stateMachine.getExtendedState().getVariables());
			nextTasks.add(nextTask);
		});
		return new ArrayList<>(nextTasks);
	}
	public ProcessFlow updateProcessFlow(TaskFlow taskFlow) {
		ProcessFlow processFlow = processFlowConvert(stateMachine);
		processFlow.processFlowDate(OffsetDateTime.now()).state(ProcessFlowStateType.ACTIVE).type("ProcessFlow");
		processFlow.getLinks().setExistingTaskEditable(taskFlow.getLinks().getExistingTaskEditable());
		processFlow.getLinks().setNextTaskstoBePerformed(taskFlow.getLinks().getNextTaskstoBePerformed());

		if (!CollectionUtils.isEmpty(taskFlow.getRelatedEntity())) {
			processFlow.setRelatedEntity(taskFlow.getRelatedEntity());
		}
		if (!CollectionUtils.isEmpty(taskFlow.getRelatedParty())) {
			processFlow.setRelatedParty(taskFlow.getRelatedParty());
		}

		return processFlow;
	}
	/**
	 * Process flow convert.
	 *
	 * @param source the source
	 * @return the process flow
	 */
	public ProcessFlow processFlowConvert(final StateMachine<String, String> source) {
		LOGGER.info("converting process instance: {}", source.getId());

		final ProcessFlow target = new ProcessFlow();
		target.setId(source.getUuid().toString());
		target.setProcessFlowSpecification(source.getId());
		convertFromVariablesForProcessFlow(source, target);
		target.setLinks(new Links().nextTaskstoBePerformed(createNextTasks(source.getUuid().toString())));
		return target;
	}
	/**
	 * Convert from variables for process flow.
	 *
	 * @param source the source
	 * @param target the target
	 */
	@SuppressWarnings("unchecked")
	public void convertFromVariablesForProcessFlow(final StateMachine<String, String> source,
			final ProcessFlow target) {
		Map<Object, Object> valueMap = source.getExtendedState().getVariables();

		try {
			valueMap.forEach((key, value) -> {
				if (value == null) {
					return;
				}
				LOGGER.debug("key - {}\nvalue - {}", key, value);
				switch (key.toString()) {
				case ProcessConstants.CHARACTERISTIC:
					final List<Characteristic> characteristicValue = (List<Characteristic>) value;
					if (target.getCharacteristic() == null) {
						target.setCharacteristic(characteristicValue);
					} else {
						target.getCharacteristic().addAll(characteristicValue);
					}
					break;
				case ProcessConstants.CHANNEL:
					final List<ChannelRef> channelValue = (List<ChannelRef>) value;
					target.setChannel(channelValue);
					break;
				case ProcessConstants.RELATED_PARTY:
					final List<RelatedParty> relatedPartyValue = (List<RelatedParty>) value;
					target.setRelatedParty(relatedPartyValue);
					break;
				case ProcessConstants.RELATED_ENTITY:
					final List<RelatedEntity> relatedEntityValue = (List<RelatedEntity>) value;
					target.setRelatedEntity(relatedEntityValue);
					break;
				default:
					LOGGER.warn("No variable fetched");
				}
			});
		} catch (NullPointerException e) {
			LOGGER.warn("values cannot be set to target", e);
		}
	}
	/**
	 * Task flow convert.
	 *
	 * @param source the source
	 * @return the task flow
	 */
	public TaskFlow taskFlowConvert(final StateMachineTransition source) {
		if (null == source) {
			return null;
		}
		LOGGER.info("converting task: {}", source.getTaskDefinitionId());
		final TaskFlow target = new TaskFlow();
		target.setId(source.getTaskDefinitionId());
		target.setTaskFlowSpecification(source.getTaskDefinitionKey());
		List<TaskLink> nextTaskList = createNextTasks(source.getProcessInstanceId());
		target.setLinks(new Links().nextTaskstoBePerformed(nextTaskList));
		target.setPriority(50);
		convertFromVariablesForTaskFlow(target);
		return target;
	}
}