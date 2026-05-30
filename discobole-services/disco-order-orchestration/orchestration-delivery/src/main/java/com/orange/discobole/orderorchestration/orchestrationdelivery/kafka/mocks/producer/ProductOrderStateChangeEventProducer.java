// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.mocks.producer;

import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductOrderStateChangeEventProducer {
    private final KafkaTemplate<String, ProductOrderStateChangeEvent> kafkaTemplate;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOrderStateChangeEventProducer(KafkaTemplate<String, ProductOrderStateChangeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(ProductOrderStateChangeEvent event) {
        kafkaTemplate.send("disco.order-management.productOrderStateChange-event", event);
    }

}