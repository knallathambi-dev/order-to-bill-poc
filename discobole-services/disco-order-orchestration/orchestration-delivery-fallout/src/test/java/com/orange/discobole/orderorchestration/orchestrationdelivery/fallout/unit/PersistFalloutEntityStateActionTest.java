// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.unit;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service.action.PersistFalloutEntityStateAction;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.util.StateMachineUtil;
import com.orange.discobole.processflow.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

class PersistFalloutEntityStateActionTest {

    @Mock
    private FalloutRepository falloutRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private PersistFalloutEntityStateAction persistFalloutEntityStateAction;

    @Mock
    private StateContext<String, String> context;

    @Mock
    private StateMachine<String, String> stateMachine;

    @Mock
    private Transition<String, String> transition;

    @Mock
    private org.springframework.statemachine.state.State<String, String> targetState;

    private FalloutIncident falloutIncident;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        falloutIncident = new FalloutIncident();
        falloutIncident.setId("1");
        when(context.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.getUuid()).thenReturn(UUID.randomUUID());
        when(context.getTransition()).thenReturn(transition);
        when(transition.getTarget()).thenReturn(targetState);
        when(targetState.getId()).thenReturn("Canceled");  // Mock the whole chain to return "Canceled"
    }

    @Test
    void givenReExecutionAction_whenApply_thenReturnsEmptyMono() {
        // Use mockStatic to mock the static method
        try (MockedStatic<StateMachineUtil> mockedStatic = Mockito.mockStatic(StateMachineUtil.class)) {
            // Given
            mockedStatic.when(() -> StateMachineUtil.isReExecutionAction(context)).thenReturn(true);

            // When
            Mono<Void> result = persistFalloutEntityStateAction.apply(context);

            // Then
            StepVerifier.create(result)
                    .verifyComplete();

            verifyNoInteractions(falloutRepository, eventPublisher);
        }

    }

    @Test
    void givenNonExistentFallout_whenApply_thenThrowsNotFoundException() {
        // Given
        try (MockedStatic<StateMachineUtil> mockedStatic = Mockito.mockStatic(StateMachineUtil.class)) {
            // Given
            mockedStatic.when(() -> StateMachineUtil.isReExecutionAction(context)).thenReturn(false);
            when(falloutRepository.findById(anyString())).thenReturn(Optional.empty());

            // When/Then
            assertThrows(NotFoundException.class, () -> {
                persistFalloutEntityStateAction.apply(context).block();
            });

            verify(falloutRepository).findById(anyString());
            verifyNoMoreInteractions(falloutRepository, eventPublisher);
        }
    }

    @Test
    void givenExistingFalloutAndTransitionToCanceled_whenApply_thenSavesFalloutAndPublishesEvent() {
        // Given
        try (MockedStatic<StateMachineUtil> mockedStatic = Mockito.mockStatic(StateMachineUtil.class)) {
            // Given
            mockedStatic.when(() -> StateMachineUtil.isReExecutionAction(context)).thenReturn(false);
            when(falloutRepository.findById(anyString())).thenReturn(Optional.of(falloutIncident));

            // When
            Mono<Void> result = persistFalloutEntityStateAction.apply(context);

            // Then
            StepVerifier.create(result)
                    .verifyComplete();

            ArgumentCaptor<FalloutIncident> falloutCaptor = ArgumentCaptor.forClass(FalloutIncident.class);
            verify(falloutRepository).findById(anyString());
            verify(falloutRepository).save(falloutCaptor.capture());
            verify(eventPublisher).publishEvent(eq(CDCEvent.FALLOUT_STATE_CHANGE_EVENT), eq(falloutIncident));
            assertEquals("Canceled", falloutCaptor.getValue().getState().getValue());
        }
    }
}