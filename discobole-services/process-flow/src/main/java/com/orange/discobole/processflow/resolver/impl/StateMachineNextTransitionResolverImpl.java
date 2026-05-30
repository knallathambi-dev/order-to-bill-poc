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

package com.orange.discobole.processflow.resolver.impl;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.region.Region;
import org.springframework.statemachine.state.RegionState;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.resolver.StateMachineNextTransitionResolver;

@Component
public class StateMachineNextTransitionResolverImpl implements StateMachineNextTransitionResolver<String, String> {

	@Override
	public List<Transition<String, String>> getAvailableTask(StateMachine<String, String> stateMachine) {
		if (stateMachine.getState() instanceof RegionState) {

			RegionState<String, String> regionState = (RegionState<String, String>) stateMachine.getState();
			List<Transition<String, String>> regionTransitions = new ArrayList<>();
			for (Region<String, String> region : regionState.getRegions()) {
				StateMachine<String, String> regionSm = (ObjectStateMachine) region;
				regionTransitions.addAll(stateMachine.getTransitions().stream()
						.filter(t -> isTransitionSourceFromCurrentState(t, regionSm))
						.filter(t -> evaluateGuardCondition(regionSm, t)).collect(toList()));
			}
			return regionTransitions;
		} else {
			return stateMachine.getTransitions().stream()
					.filter(t -> isTransitionSourceFromCurrentState(t, stateMachine))
					.filter(t -> evaluateGuardCondition(stateMachine, t)).collect(toList());
		}
	}

	private boolean isTransitionSourceFromCurrentState(Transition<String, String> transition,
			StateMachine<String, String> stateMachine) {

		return stateMachine.getState().getId().equals(transition.getSource().getId());
	}

	private boolean evaluateGuardCondition(StateMachine<String, String> stateMachine,
			Transition<String, String> transition) {

		if (transition.getGuard() == null) {
			return true;
		}

		StateContext<String, String> context = makeStateContext(stateMachine, transition);

		try {
			return transition.getGuard().apply(context).block();
		} catch (Exception e) {
			return false;
		}
	}

	private DefaultStateContext<String, String> makeStateContext(StateMachine<String, String> stateMachine,
			Transition<String, String> transition) {

		return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, stateMachine.getExtendedState(),
				transition, stateMachine, stateMachine.getState(), transition.getTarget(), null);
	}

}
