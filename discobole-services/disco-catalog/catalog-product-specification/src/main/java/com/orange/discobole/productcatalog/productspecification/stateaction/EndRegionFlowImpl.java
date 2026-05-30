// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.stateaction;

import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import reactor.core.publisher.Mono;


@Component("endRegionFlow")
public class EndRegionFlowImpl implements StateMachineStateAction<String, String> {
    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        context.getStateMachine().getTransitions().clear();
        context.getStateMachine().stop();
        return Mono.empty();
    }
}
