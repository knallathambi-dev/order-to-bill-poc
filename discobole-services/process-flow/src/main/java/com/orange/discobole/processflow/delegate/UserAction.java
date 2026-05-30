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

package com.orange.discobole.processflow.delegate;

import java.util.List;
import java.util.Map;

import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;

/**
 * Contract to perform the user task
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public interface UserAction {

	/**
	 * method definition to perform user task.
	 *
	 * @param stateMachine   State Machine object
	 * @param taskFlowUpdate task request with Channel, System, Related Party,
	 *                       Characteristic and Correlation id
	 * @return map which contains all variable. It is used to complete the task so
	 *         that variables can be accessed in next task(s)
	 * @throws ParameterException is thrown if required parameters are missing or
	 *                            invalid
	 */
	Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException;

	/**
	 * method definition to get required characteristics for task to be performed.
	 *
	 * @param stateMachineTransition execution id of process
	 * @return List of {@code CharacteristicSpecification}
	 */
	List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables);

}
