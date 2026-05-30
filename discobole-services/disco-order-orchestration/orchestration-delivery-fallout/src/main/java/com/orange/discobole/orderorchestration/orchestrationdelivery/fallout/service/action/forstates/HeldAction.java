// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service.action.forstates;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Component("HeldAction")
@RequiredArgsConstructor
public class HeldAction implements StateMachineStateAction<String, String> {

    private final FalloutRepository falloutRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        return Mono.empty();
    }
}
