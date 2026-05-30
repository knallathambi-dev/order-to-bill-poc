// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.config;

import com.orange.discobole.ordermanagement.event.om.ProductOrderAttributeValueChangeEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import com.orange.discobole.ordermanagement.orderfollowup.service.kafka.consumer.ProductOrderAttributeValueChangeEventConsumer;
import com.orange.discobole.ordermanagement.orderfollowup.service.kafka.consumer.ProductOrderEventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class ConsumerConfiguration {

    @Bean
    public Consumer<Message<ProductOrderStateChangeEvent>> productOrderEvent(ProductOrderEventService productOrderEventService) {
        log.info("Initializing ProductOrderEvent consumer");
        return new ProductOrderEventConsumer(productOrderEventService);
    }

    @Bean
    public Consumer<Message<ProductOrderAttributeValueChangeEvent>> productOrderAttributeValueChangeEvent(ProductOrderEventService productOrderEventService) {
        log.info("Initializing ProductOrderAttributeValueChangeEvent consumer");
        return new ProductOrderAttributeValueChangeEventConsumer(productOrderEventService);
    }
}