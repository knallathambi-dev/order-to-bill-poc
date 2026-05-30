// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.kafka.producer.impl;

import com.orange.discobole.ordermanagement.commons.enumeration.EventType;
import com.orange.discobole.ordermanagement.event.om.ProductOrderPayloadEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.ordermanagement.orderinventory.config.Constants;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.service.kafka.producer.ProductOrderEventProducer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.UUID;

import static com.orange.discobole.ordermanagement.commons.constant.ChannelConstant.PRODUCT_ORDER_EVENT;
import static com.orange.discobole.ordermanagement.orderinventory.constant.ErrorMessages.*;

@Service
public class ProductOrderEventProducerImpl implements ProductOrderEventProducer {

    private final StreamBridge streamBridge;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOrderEventProducerImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    private static ProductOrderStateChangeEvent createProductOrderEvent(ProductOrder productOrder, EventType eventType) {
        ProductOrderPayloadEvent productOrderPayloadEvent = ProductOrderPayloadEvent.builder().productOrder(productOrder).build();

        return ProductOrderStateChangeEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(eventType.getValue())
                .event(productOrderPayloadEvent)
                .build();
    }

    private static void checkParameter(ProductOrder productOrder, EventType eventType) {
        Assert.notNull(eventType, EVENT_TYPE_MAY_NOT_BE_NULL);
        Assert.notNull(productOrder, PRODUCT_ORDER_MAY_NOT_BE_NULL);
        Assert.notNull(productOrder.getId(), PRODUCT_ORDER_ID_MAY_NOT_BE_NULL);
    }

    @Override
    public void publishEvent(ProductOrder productOrder, EventType eventType) {
        checkParameter(productOrder, eventType);
        ProductOrderStateChangeEvent productOrderEvent = createProductOrderEvent(productOrder, eventType);
        String partitionKey = productOrder.getId();

        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderEvent).setHeader(Constants.PARTITION_KEY, partitionKey).build();

        streamBridge.send(PRODUCT_ORDER_EVENT, message);
    }
}