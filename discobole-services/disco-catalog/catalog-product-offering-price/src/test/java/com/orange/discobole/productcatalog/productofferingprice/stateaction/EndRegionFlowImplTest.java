// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.stateaction;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.transition.Transition;

class EndRegionFlowImplTest {

	@InjectMocks
	private EndRegionFlowImpl endRegionFlowImpl;

	@Test
	void endRegionFlowImplTest() {
		endRegionFlowImpl = new EndRegionFlowImpl();
		StateContext<String, String> context = Mockito.mock(StateContext.class);
		StateMachine<String, String> sm = Mockito.mock(StateMachine.class);
		Collection<Transition<String, String>> transCollection = Mockito.mock(Collection.class);
		Mockito.when(context.getStateMachine()).thenReturn(sm);
		Mockito.when(sm.getTransitions()).thenReturn(transCollection);
		endRegionFlowImpl.apply(context);
		Assertions.assertNotNull(context);
	}

}
