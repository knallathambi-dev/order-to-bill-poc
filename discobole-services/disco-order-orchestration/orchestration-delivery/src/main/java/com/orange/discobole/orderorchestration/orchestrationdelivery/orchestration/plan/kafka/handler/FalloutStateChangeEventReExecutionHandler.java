// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.kafka.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.orderorchestration.exception.model.CoodNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.kafka.handler.exceuter.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.COOD_NOT_FOUND_EXCEPTION;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.*;

@Component
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class FalloutStateChangeEventReExecutionHandler {
    private final Map<String, KafkaEventReExecutionExecutor<?>> reExecutionTopicMap;

    private final ObjectMapper objectMapper;

    public FalloutStateChangeEventReExecutionHandler(ProductOrderStateChangeEventExecutor productOrderStateChangeEventExecutor,
                                                     OrchestrationPlanStateChangeEventReExecutionExecutor orchestrationPlanStateChangeEventReExecutionExecutor,
                                                     OrchestrationPlanNodeStateChangeEventReExecutionExecuter orchestrationPlanNodeStateChangeEventReExecutionExecuter,
                                                     DeliveryOrderEventReExecutionExecutor deliveryOrderEventReExecutionExecutor,
                                                     ObjectMapper objectMapper) {

        this.reExecutionTopicMap = Map.of(
                PRODUCT_ORDER_STATE_CHANGE_DLT_TOPIC, productOrderStateChangeEventExecutor,
                ORCHESTRATION_PLAN_NODE_STATE_CHANGE_DLT_TOPIC, orchestrationPlanNodeStateChangeEventReExecutionExecuter,
                ORCHESTRATION_PLAN_STATE_CHANGE_DLT_TOPIC, orchestrationPlanStateChangeEventReExecutionExecutor,
                DELIVERY_ORDER_DLT_TOPIC, deliveryOrderEventReExecutionExecutor
        );
        this.objectMapper = objectMapper;
    }

    public <T> void handle(String kafkaTopic, ObjectCharacteristic characteristic) {
        KafkaEventReExecutionExecutor<T> executor = (KafkaEventReExecutionExecutor<T>) getReExecutionTopic(kafkaTopic);
        executor.execute(objectMapper.convertValue(characteristic.getValue(), executor.getEventType()));
    }

    private KafkaEventReExecutionExecutor<?> getReExecutionTopic(String kafkaTopic) {
        return Optional.ofNullable(reExecutionTopicMap.get(kafkaTopic))
                .orElseThrow(() -> new CoodNotFoundException(COOD_NOT_FOUND_EXCEPTION, "No destination kafka topic for this source topic [%s]".formatted(kafkaTopic)));
    }

}
