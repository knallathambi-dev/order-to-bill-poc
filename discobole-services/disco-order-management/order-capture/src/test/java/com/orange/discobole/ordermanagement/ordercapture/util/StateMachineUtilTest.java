// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;


class StateMachineUtilTest {

    public static final String KEY_NAME = RandomStringUtils.randomAlphabetic(5);
    public static final String VALUE = RandomStringUtils.randomAlphabetic(5);
    public static final String GUARD_NAME = RandomStringUtils.randomAlphabetic(5);
    public static final String MESSAGE = RandomStringUtils.randomAlphabetic(5);

    @Test
    void getStringValue_ValidKey_ReturnsValue() {
        //given
        StateContext<String, String> context = mockStatContext();
        context.getExtendedState().getVariables().put(KEY_NAME, VALUE);

        //when
        String result = StateMachineUtil.getStringValue(context, KEY_NAME);

        //then
        Assertions.assertEquals(VALUE, result);
    }

    @Test
    void getBooleanValue_ValidKey_ReturnsValue() {
        //given
        StateContext<String, String> context = mockStatContext();
        context.getExtendedState().getVariables().put(KEY_NAME, true);

        //when
        Boolean result = StateMachineUtil.getBooleanValue(context, KEY_NAME, false);

        //then
        Assertions.assertTrue(result);
    }

    @Test
    void getObjectValue_ValidKeyAndClass_ReturnsValue() {
        //given
        StateContext<String, String> context = mockStatContext();
        context.getExtendedState().getVariables().put(KEY_NAME, VALUE);

        //when
        String result = StateMachineUtil.getObjectValue(context, KEY_NAME, String.class);

        //then
        Assertions.assertEquals(VALUE, result);
    }

    @Test
    void setGuardContext_ValidGuardName_SetsGuardResult() {
        //given
        StateContext<String, String> context = mockStatContext();

        //when
        StateMachineUtil.setGuardContext(context, true, GUARD_NAME);

        //then
        Map<String, Boolean> guardMap = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), StateMachineUtil.GUARD);
        Assertions.assertTrue(guardMap.containsKey(GUARD_NAME));
        Assertions.assertTrue(guardMap.get(GUARD_NAME));
    }

    @Test
    void getGuardResult_ValidGuardName_ReturnsGuardResult() {
        //given
        StateContext<String, String> context = mockStatContext();
        Map<String, Boolean> guardMap = new HashMap<>();
        guardMap.put(GUARD_NAME, true);
        context.getExtendedState().getVariables().put(StateMachineUtil.GUARD, guardMap);

        //when
        Mono<Boolean> result = StateMachineUtil.getGuardResult(context, GUARD_NAME);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @Test
    void isReExecutionAction_ReExecutionAction_ReturnsTrue() {
        //given
        StateContext<String, String> context = mockStatContext();
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, true);

        //when
        Boolean result = StateMachineUtil.isReExecutionAction(context);

        //then
        Assertions.assertTrue(result);
    }

    @Test
    void setDescriptionContext_ValidMessage_SetsDescription() {
        //given
        StateContext<String, String> context = mockStatContext();

        //when
        StateMachineUtil.setDescriptionContext(context, MESSAGE);

        //then
        String result = (String) context.getExtendedState().getVariables().get(StateMachineUtil.DESCRIPTION);
        Assertions.assertEquals(MESSAGE, result);
    }

    private DefaultStateContext<String, String> mockStatContext() {
        ExtendedState extendedState = new DefaultExtendedState();

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}