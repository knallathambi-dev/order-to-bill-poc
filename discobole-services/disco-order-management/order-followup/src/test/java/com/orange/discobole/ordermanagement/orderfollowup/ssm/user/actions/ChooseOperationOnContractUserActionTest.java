// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.ssm.user.actions;

import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ChooseOperationOnContractUserActionTest {
    public static final String PROCESS_DEFINITION_KEY = RandomStringUtils.randomAlphabetic(10);

    @InjectMocks
    private ChooseOperationOnContractUserAction chooseOperationOnContractUserAction;

    @BeforeEach
    void setUp() {
        chooseOperationOnContractUserAction.init();
    }

    @Test
    @DisplayName("Given a valid characteristic, " +
            "when performing a new product state change event user action, " +
            "then return an empty variable list")
    void shouldReturnEmptyVariableListWhenValidCharacteristicProvided() {
        // Given
        StateMachineTransition transition = buildStateMachineTransition();
        TaskFlowUpdate taskFlowUpdate = buildTaskFlowUpdate();

        // When
        Map<String, Object> result = chooseOperationOnContractUserAction.perform(transition, taskFlowUpdate);

        // Then
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Given a valid task definition ID, " +
            "when retrieving required characteristics, " +
            "then return a single required characteristic")
    void shouldReturnRequiredCharacteristicsWhenTaskDefinitionIdIsValid() {
        // Given
        StateMachineTransition stateMachineTransition = buildStateMachineTransition();
        Map<Object, Object> contextVariables = new HashMap<>();

        // When
        List<CharacteristicSpecification> result =
                chooseOperationOnContractUserAction.requiredCharacteristics(stateMachineTransition, contextVariables);

        // Then
        assertEquals(1, result.size());
    }

    private StateMachineTransition buildStateMachineTransition() {
        StateMachineTransition stateMachineTransition = new StateMachineTransition();
        stateMachineTransition.setProcessDefinitionKey(PROCESS_DEFINITION_KEY);
        return stateMachineTransition;
    }

    private TaskFlowUpdate buildTaskFlowUpdate() {
        return new TaskFlowUpdate();
    }
}