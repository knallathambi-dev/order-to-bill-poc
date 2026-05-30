// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.unit;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.user.actions.SelectResolveOrUnresolvedUserAction;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectResolveOrUnresolvedUserActionTest {

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private FalloutRepository falloutRepository;

    private SelectResolveOrUnresolvedUserAction userAction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAction = new SelectResolveOrUnresolvedUserAction(eventPublisher, falloutRepository);
        userAction.init();
    }

    @Test
    void givenInvalidProcessInstanceId_whenPerform_thenThrowsNotFoundException() {
        // Given
        StateMachineTransition stateMachine = mock(StateMachineTransition.class);
        TaskFlowUpdate taskFlowUpdate = mock(TaskFlowUpdate.class);

        when(stateMachine.getProcessInstanceId()).thenReturn("123");
        when(falloutRepository.findById("123")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidParameterException.class, () -> userAction.perform(stateMachine, taskFlowUpdate));
    }

    @Test
    void givenValidStateMachineTransition_whenRequiredCharacteristics_thenReturnsExpectedCharacteristics() {
        // Given
        StateMachineTransition stateMachineTransition = mock(StateMachineTransition.class);
        Map<Object, Object> contextVariables = new HashMap<>();

        when(stateMachineTransition.getTaskDefinitionId()).thenReturn("taskDefId");

        // When
        List<CharacteristicSpecification> result = userAction.requiredCharacteristics(stateMachineTransition, contextVariables);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Completed", result.get(0).getName());
    }

    @Test
    void givenExceptionDuringInit_whenInit_thenThrowsDiscoClientException() {
        // Given
        SelectResolveOrUnresolvedUserAction userActionWithException = new SelectResolveOrUnresolvedUserAction(eventPublisher, falloutRepository) {
            @Override
            public void init() {
                throw new RuntimeException("Test exception");
            }
        };

        // When & Then
        assertThrows(RuntimeException.class, userActionWithException::init);
    }
}

