// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.exception.model.CoodNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.kafka.handler.FalloutStateChangeEventReExecutionHandler;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.kafka.handler.exceuter.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FalloutStateChangeEventReExecutionHandlerTest {

    @InjectMocks
    FalloutStateChangeEventReExecutionHandler falloutStateChangeEventReExecutionHandler;

    @Mock
    ProductOrderStateChangeEventExecutor productOrderStateChangeEventExecutor;

    @Mock
    OrchestrationPlanStateChangeEventReExecutionExecutor orchestrationPlanStateChangeEventReExecutionExecutor;

    @Mock
    OrchestrationPlanNodeStateChangeEventReExecutionExecuter orchestrationPlanNodeStateChangeEventReExecutionExecuter;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    DeliveryOrderEventReExecutionExecutor deliveryOrderEventReExecutionExecutor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenProductOrderStateChangeDLTKafkaTopicAndObjectCharacteristic_whenHandleReExecution_thenMessageSentToCorrectExecutor() {
        when(productOrderStateChangeEventExecutor.getEventType()).thenCallRealMethod();

        falloutStateChangeEventReExecutionHandler.handle(KafkaTopic.PRODUCT_ORDER_STATE_CHANGE_DLT_TOPIC, new ObjectCharacteristic());

        verify(productOrderStateChangeEventExecutor).execute(any());
        verify(objectMapper).convertValue(any(), eq(ProductOrderStateChangeEvent.class));
    }

    @Test
    void givenOrchestrationPlanStateChangeDLTKafkaTopicAndObjectCharacteristic_whenHandleReExecution_thenMessageSentToCorrectExecutor() {
        when(orchestrationPlanStateChangeEventReExecutionExecutor.getEventType()).thenCallRealMethod();

        falloutStateChangeEventReExecutionHandler.handle(KafkaTopic.ORCHESTRATION_PLAN_STATE_CHANGE_DLT_TOPIC, new ObjectCharacteristic());

        verify(orchestrationPlanStateChangeEventReExecutionExecutor).execute(any());
        verify(objectMapper).convertValue(any(), eq(OrchestrationPlanStateChangeEvent.class));

    }

    @Test
    void givenOrchestrationPlanNodeStateChangeDLTKafkaTopicAndObjectCharacteristic_whenHandleReExecution_thenMessageSentToCorrectExecutor() {
        when(orchestrationPlanNodeStateChangeEventReExecutionExecuter.getEventType()).thenCallRealMethod();

        falloutStateChangeEventReExecutionHandler.handle(KafkaTopic.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_DLT_TOPIC, new ObjectCharacteristic());

        verify(orchestrationPlanNodeStateChangeEventReExecutionExecuter).execute(any());
        verify(objectMapper).convertValue(any(), eq(OrchestrationPlanNodeStateChangeEvent.class));
    }

    @Test
    void givenDeliveryOrderItemStatuesChangeDLTKafkaTopicAndObjectCharacteristic_whenHandleReExecution_thenMessageSentToCorrectExecutor() {
        when(deliveryOrderEventReExecutionExecutor.getEventType()).thenCallRealMethod();

        falloutStateChangeEventReExecutionHandler.handle(KafkaTopic.DELIVERY_ORDER_DLT_TOPIC, new ObjectCharacteristic());

        verify(deliveryOrderEventReExecutionExecutor).execute(any());
        verify(objectMapper).convertValue(any(), eq(DeliveryOrderEvent.class));
    }

    @Test
    void givenInvalidKafkaTopic_whenHandleReExecution_thenExceptionThrown() {
        String randomId = RandomStringUtils.randomAlphabetic(10);
        ObjectCharacteristic characteristic = new ObjectCharacteristic();

        assertThatExceptionOfType(CoodNotFoundException.class)
                .isThrownBy(() -> falloutStateChangeEventReExecutionHandler.handle(randomId, characteristic));
    }

}
