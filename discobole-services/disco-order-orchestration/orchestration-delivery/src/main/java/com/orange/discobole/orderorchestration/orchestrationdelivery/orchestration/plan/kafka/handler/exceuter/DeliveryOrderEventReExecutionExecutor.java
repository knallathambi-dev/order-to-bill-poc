// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.kafka.handler.exceuter;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNodeNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class DeliveryOrderEventReExecutionExecutor implements KafkaEventReExecutionExecutor<DeliveryOrderEvent> {

    private final KafkaTemplate<Object, DeliveryOrderEvent> kafkaTemplate;
    private final OrchestrationPlanRepository orchestrationPlanRepository;

    @Override
    public void execute(DeliveryOrderEvent event) {
        kafkaTemplate.send(KafkaTopic.DELIVERY_ORDER_TOPIC, event);
    }

    @Override
    public Class<DeliveryOrderEvent> getEventType() {
        return DeliveryOrderEvent.class;
    }
}
