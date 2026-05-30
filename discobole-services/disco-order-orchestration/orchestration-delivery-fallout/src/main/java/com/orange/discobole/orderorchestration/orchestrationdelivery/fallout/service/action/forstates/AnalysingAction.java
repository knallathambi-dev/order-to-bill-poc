// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service.action.forstates;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.util.StateMachineUtil;
import com.orange.discobole.processflow.exception.NotFoundException;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Component("AnalysingAction")
@RequiredArgsConstructor
public class AnalysingAction implements StateMachineStateAction<String, String> {

    private final FalloutRepository falloutRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (!context.getTransition().getSource().getId().equals(State.ANALYSING.getValue())) {
            return Mono.empty();
        }
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }

        FalloutIncident fallout = falloutRepository.findById(context.getStateMachine().getUuid().toString())
                .orElseThrow(() -> new NotFoundException("Fallout not found with id: %s".formatted(context.getStateMachine().getId())));
        fallout.setState(State.fromValue(context.getTransition().getTarget().getId()));

        //todo presist charactaristics from cood
        falloutRepository.save(fallout);
        eventPublisher.publishEvent(CDCEvent.FALLOUT_STATE_CHANGE_EVENT, fallout);
        return Mono.empty();
    }
}
