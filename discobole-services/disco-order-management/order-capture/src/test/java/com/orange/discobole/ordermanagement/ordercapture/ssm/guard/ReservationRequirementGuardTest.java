// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ReservationRequirementGuardTest {
    public static final String DEFAULT_RELATED_RESOURCES_ID_1 = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_RELATED_RESOURCES_ID_2 = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ID1 = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ID2 = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(10);
    @InjectMocks
    private ReservationRequirementGuard reservationRequirementGuard;

    @DisplayName("given re-executed reservation requirement guard " +
            "when check if reservation was needed " +
            "then return true")
    @Test
    void testReservationRequirementGuardReExecution() {
        //given
        StateContext<String, String> context = mockStatContext(createProductOrderItemLogicalResourcesMap(), Collections.emptyList());
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.RESERVATION_REQUIREMENT_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = reservationRequirementGuard.apply(context);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given reservation not needed " +
            "when check if reservation was needed " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testReservationNotNeeded() {
        //given
        StateContext<String, String> context = mockStatContext(Collections.emptyMap(), Collections.emptyList());

        //when
        Mono<Boolean> result = reservationRequirementGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.RESERVATION_REQUIREMENT_GUARD);

        //then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
    }

    @DisplayName("given the logical resources reservation was needed " +
            "when check if reservation was needed " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testLogicalResourcesReservationWasNeeded() {
        //given
        StateContext<String, String> context = mockStatContext(createProductOrderItemLogicalResourcesMap(), Collections.emptyList());

        //when
        Mono<Boolean> result = reservationRequirementGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.RESERVATION_REQUIREMENT_GUARD);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given the physical resources reservation was needed " +
            "when check if reservation was needed " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testPhysicalResourcesReservationWasNeeded() {
        //given
        StateContext<String, String> context = mockStatContext(Collections.emptyMap(), Collections.singletonList(PRODUCT_ORDER_ITEM_ID));

        //when
        Mono<Boolean> result = reservationRequirementGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.RESERVATION_REQUIREMENT_GUARD);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given the physical and logical resources reservation was needed " +
            "when check if reservation was needed " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testPhysicalAndLogicalResourcesReservationWasNeeded() {
        //given
        StateContext<String, String> context = mockStatContext(createProductOrderItemLogicalResourcesMap(), Collections.singletonList(PRODUCT_ORDER_ITEM_ID));

        //when
        Mono<Boolean> result = reservationRequirementGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.RESERVATION_REQUIREMENT_GUARD);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    Map<String, List<String>> createProductOrderItemLogicalResourcesMap() {
        Map<String, List<String>> productItemRelatedResourcesMap = new HashMap<>();
        productItemRelatedResourcesMap.put(PRODUCT_ORDER_ID1, List.of(DEFAULT_RELATED_RESOURCES_ID_1));
        productItemRelatedResourcesMap.put(PRODUCT_ORDER_ID2, List.of(DEFAULT_RELATED_RESOURCES_ID_2));
        return productItemRelatedResourcesMap;
    }

    private DefaultStateContext<String, String> mockStatContext(Map<String, List<String>> productOrderItemLogicalResourcesMap, List<String> physicalProductOrderItems) {
        ExtendedState extendedState = new DefaultExtendedState();

        extendedState.getVariables().put(OrderCaptureConstants.PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES, productOrderItemLogicalResourcesMap);
        extendedState.getVariables().put(OrderCaptureConstants.PHYSICAL_PRODUCT_ITEM_ID_LIST, physicalProductOrderItems);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}