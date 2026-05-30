// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config;

import com.orange.discobole.ordermanagement.event.om.ProductOrderCommand;
import com.orange.discobole.ordermanagement.orderinventory.service.ProductOrderService;
import com.orange.discobole.ordermanagement.orderinventory.service.kafka.consumer.OrchestrationPlanNodeEventConsumer;
import com.orange.discobole.ordermanagement.orderinventory.service.kafka.consumer.ProductOrderCommandConsumer;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class ConsumerConfiguration {

    @Bean
    public Consumer<Message<ProductOrderCommand>> productOrderCommand(ProductOrderService productOrderService) {
        return new ProductOrderCommandConsumer(productOrderService);
    }

    @Bean
    public Consumer<Message<OrchestrationPlanNodeStateChangeEvent>> orchestrationPlanNodeEvent(ProductOrderService productOrderService) {
        return new OrchestrationPlanNodeEventConsumer(productOrderService);
    }
}