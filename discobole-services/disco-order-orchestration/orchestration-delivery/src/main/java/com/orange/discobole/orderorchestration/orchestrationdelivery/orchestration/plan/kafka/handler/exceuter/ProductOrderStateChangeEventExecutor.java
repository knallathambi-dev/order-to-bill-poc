// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.kafka.handler.exceuter;

import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class ProductOrderStateChangeEventExecutor implements KafkaEventReExecutionExecutor<ProductOrderStateChangeEvent> {

    private final KafkaTemplate<String, ProductOrderStateChangeEvent> kafkaTemplate;

    @Override
    public void execute(ProductOrderStateChangeEvent event) {
        kafkaTemplate.send(KafkaTopic.PRODUCT_ORDER_STATE_CHANGE_TMR_TOPIC, event);
    }

    @Override
    public Class<ProductOrderStateChangeEvent> getEventType() {
        return ProductOrderStateChangeEvent.class;
    }
}
