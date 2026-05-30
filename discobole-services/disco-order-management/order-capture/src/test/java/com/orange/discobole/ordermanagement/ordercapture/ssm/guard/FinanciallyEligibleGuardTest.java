// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.commons.dto.party.management.Party;
import com.orange.discobole.ordermanagement.commons.dto.party.management.PartyCreditProfile;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.service.*;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.state.ObjectState;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.statemachine.transition.InitialTransition;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanciallyEligibleGuardTest {

    public static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_RELATED_PARTY_ROLE = RandomStringUtils.randomAlphabetic(5);
    public static final String START_STATE = "start";
    public static final String EVENT = "event";

    @Mock
    private SettingsService settingsService;
    @Mock
    private PartyManagementService partyManagementService;
    @Mock
    private ProductOrderService productOrderService;
    @Mock
    private ProductInventoryService productInventoryService;
    @Mock
    private ResourceInventoryService resourceInventoryService;
    @InjectMocks
    private FinanciallyEligibleGuard financiallyEligibleGuard;


    @Test
    @DisplayName("Given eligibility guard is re-executed, " +
            "when applying the guard, " +
            "then it should return true")
    void shouldReturnTrueWhenEligibilityGuardReExecuted() {
        // Given
        StateContext<String, String> context = mockStatContext();
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.FINANCIALLY_ELIGIBLE_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Boolean> result = financiallyEligibleGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Given financial eligibility check is enabled, " +
            "when check is eligible, " +
            "then it should return true")
    void shouldReturnTrueWhenEligibilityCheckIsEnabled() {
        // Given

        SettingsEntity settings = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settings);
        StateContext<String, String> context = mockStatContext();
        Party party = Party.builder()
                .creditRating(List.of(PartyCreditProfile.builder()
                        .ratingScore(800)
                        .build()))
                .build();
        when(partyManagementService.getPartyById(any())).thenReturn(party);

        // When
        Mono<Boolean> result = financiallyEligibleGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Given financial eligibility check is disabled, " +
            "when check is eligible, " +
            "then it should return true")
    void shouldReturnTrueWhenEligibilityCheckIsDisabled() {
        // Given

        SettingsEntity settings = createSettings(false);
        when(settingsService.getSettings()).thenReturn(settings);
        StateContext<String, String> context = mockStatContext();

        // When
        Mono<Boolean> result = financiallyEligibleGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Given financial eligibility check is enabled and rating score is less then fixed score , " +
            "when check is eligible, " +
            "then it should return true")
    void shouldReturnTrueWhenEligibilityCheckIsEnabledAndHavingRatingScoreLessThenFixedScore() {
        // Given

        SettingsEntity settings = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settings);
        StateContext<String, String> context = mockStatContext();
        Party party = Party.builder()
                .creditRating(List.of(PartyCreditProfile.builder()
                        .ratingScore(400)
                        .build()))
                .build();
        when(partyManagementService.getPartyById(any())).thenReturn(party);
        doNothing().when(productOrderService).updateProductOrderInventoryState(any(), any());
        doNothing().when(productInventoryService).abortProducts(any());
        doNothing().when(resourceInventoryService).rollBackReservedResource(any());

        // When
        Mono<Boolean> result = financiallyEligibleGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Given an exception occurs in financial eligibility, " +
            "when check eligibility, " +
            "then it should return false and set description to INTERNAL_SERVER_ERROR")
    void shouldReturnFalseWhenExceptionInFinancialEligibilityService() {
        // Given
        when(partyManagementService.getPartyById(anyString())).thenThrow(RuntimeException.class);
        StateContext<String, String> context = mockStatContext();
        SettingsEntity settings = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settings);

        // When
        Mono<Boolean> result = financiallyEligibleGuard.apply(context);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
        Assertions.assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, description);
    }

    private ProductOrder createProductOrder() {
        ProductOrderItem productOrderItem = ProductOrderItem.builder()
                .id("123")
                .action(ItemActionType.ADD)
                .state(ProductOrderItemStateType.DRAFT)
                .itemPrice(List.of(OrderPrice.builder()
                        .productOfferingPrice(InstallmentCharge.builder()
                                .id("123")
                                .build()
                        )
                        .build()))
                .product(Product.builder()
                        .id("234")
                        .realizingResource(Collections.singletonList(ResourceRef.builder()
                                .id("456")
                                .atType(OrderCaptureConstants.LOGICAL_RESOURCE)
                                .build()))
                        .productRelationship(List.of(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRelationship.builder()
                                .relationshipType("hasParent")
                                .product(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef.builder()
                                        .id("657")
                                        .build())
                                .build()))
                        .atType(OrderCaptureConstants.PRODUCT_TYPE)
                        .build())
                .build();
        ProductOrderItem migrateItem = ProductOrderItem.builder()
                .id("123")
                .action(ItemActionType.MIGRATE)
                .state(ProductOrderItemStateType.DRAFT)
                .itemPrice(List.of(OrderPrice.builder()
                        .productOfferingPrice(InstallmentCharge.builder()
                                .id("123")
                                .build()
                        )
                        .build()))
                .product(Product.builder()
                        .id("234")
                        .realizingResource(Collections.singletonList(ResourceRef.builder()
                                .id("456")
                                .atType(OrderCaptureConstants.LOGICAL_RESOURCE)
                                .build()))
                        .productRelationship(List.of(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRelationship.builder()
                                .relationshipType("hasParent")
                                .product(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef.builder()
                                        .id("657")
                                        .build())
                                .build()))
                        .atType(OrderCaptureConstants.PRODUCT_TYPE)
                        .build())
                .productOrderItemRelationship(List.of(OrderItemRelationship.builder()
                        .relationshipType(RelationshipType.MIGRATEFROM)
                        .id("123")
                        .build()))
                .build();
        return ProductOrder.builder()
                .id("897")
                .href("href")
                .creationDate(Instant.now())
                .state(ProductOrderStateType.DRAFT)
                .productOrderItem(List.of(productOrderItem, migrateItem))
                .atType(OrderCaptureConstants.PRODUCT_ORDER_TYPE)
                .build();
    }

    private StateContext<String, String> mockStatContext() {
        ExtendedState extendedState = new DefaultExtendedState();


        RelatedParty relatedParty = new RelatedParty()
                .id(DEFAULT_RELATED_PARTY_ID)
                .role(DEFAULT_RELATED_PARTY_ROLE);

        extendedState.getVariables().put(OrderCaptureConstants.RELATED_PARTY, relatedParty);

        extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, createProductOrder());

        State<String, String> state = new ObjectState<>(START_STATE);
        Transition<String, String> transition = new InitialTransition<>(state);
        Message<String> event = MessageBuilder.withPayload(EVENT).build();
        MessageHeaders header = new MessageHeaders(new HashMap<>());
        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(Collections.emptyList(), Collections.emptyList(), state, transition, event,
                extendedState, UUID.randomUUID());

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, event, header, extendedState, transition, stateMachine, state,
                state, null);
    }

    private SettingsEntity createSettings(boolean isEnabledFlag) {
        return SettingsEntity.builder()
                .checkFinancialEligibilityEnabled(isEnabledFlag)
                .build();
    }


}