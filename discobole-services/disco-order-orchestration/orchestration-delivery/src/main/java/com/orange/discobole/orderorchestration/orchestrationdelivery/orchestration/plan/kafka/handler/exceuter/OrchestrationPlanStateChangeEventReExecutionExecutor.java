// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.kafka.handler.exceuter;

import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class OrchestrationPlanStateChangeEventReExecutionExecutor implements KafkaEventReExecutionExecutor<OrchestrationPlanStateChangeEvent> {
    private final KafkaTemplate<String, OrchestrationPlanStateChangeEvent> kafkaTemplate;

    @Override
    public void execute(OrchestrationPlanStateChangeEvent event) {
        OrchestrationPlan orchestrationPlan = event.getEvent().getOrchestrationPlan();
        orchestrationPlan.setState(orchestrationPlan.getPreviousState());
        kafkaTemplate.send(KafkaTopic.ORCHESTRATION_PLAN_STATE_CHANGE_TOPIC, event);
    }

    @Override
    public Class<OrchestrationPlanStateChangeEvent> getEventType() {
        return OrchestrationPlanStateChangeEvent.class;
    }
}
