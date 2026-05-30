// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.config;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
import com.orange.discobole.processflow.util.AutomateState;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AutomateStatesImpl implements AutomateState {

    private static final String CREATE_EVENT_NAME = "AUTOMATE-event";
    private static final String ANALYSING_EVENT_NAME = "AUTOMATE";
    private static final String HELD_EVENT_NAME = "resolutionRequiredEvent";

    @Override
    public Void automateState(StateMachine<String, String> stateMachine) {
        if (stateMachine.getState().getId().contains(State.CREATED.getValue())) {
            stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(CREATE_EVENT_NAME).build())).subscribe();
        }
        if (stateMachine.getState().getId().contains(State.ANALYSING.getValue())) {
            stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(ANALYSING_EVENT_NAME).build())).subscribe();
        }

        if (stateMachine.getState().getId().contains(State.HELD.getValue())) {
            stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(HELD_EVENT_NAME).build())).subscribe();
        }
        return null;

    }
}