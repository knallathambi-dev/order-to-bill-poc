// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.producer.impl;


import com.orange.discobole.ordermanagement.event.om.Command.EventType;
import com.orange.discobole.ordermanagement.event.om.ProductOrderCommand;
import com.orange.discobole.ordermanagement.event.om.ProductOrderPayloadCommand;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.producer.ProductOrderCommandProducer;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.UUID;

import static com.orange.discobole.ordermanagement.commons.constant.ChannelConstant.PRODUCT_ORDER_COMMAND;
import static com.orange.discobole.processflow.constant.ProcessFlowConstants.PARTITION_KEY;

@Service
@Slf4j
public class ProductOrderCommandProducerImpl implements ProductOrderCommandProducer {

    private final StreamBridge streamBridge;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOrderCommandProducerImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    private static ProductOrderCommand getProductOrderCommand(ProductOrder productOrderDTO, EventType eventType) {
        ProductOrderPayloadCommand productOrderPayloadEvent = ProductOrderPayloadCommand
                .builder()
                .productOrder(productOrderDTO)
                .build();

        return ProductOrderCommand
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(eventType)
                .event(productOrderPayloadEvent)
                .build();
    }

    private static void checkParameter(ProductOrder productOrderDTO, EventType eventType) {
        Assert.notNull(eventType, ExceptionMessage.EVENT_TYPE_MAY_NOT_BE_NULL);
        Assert.notNull(productOrderDTO, ExceptionMessage.PRODUCT_ORDER_MAY_NOT_BE_NULL);
        Assert.notNull(productOrderDTO.getId(), ExceptionMessage.PRODUCT_ORDER_ID_MAY_NOT_BE_NULL);
    }

    @Override
    public void publishCommand(ProductOrder productOrderDTO, EventType eventType) {
        try {
            log.debug("Publishing command message on topic disco.order-management.productOrderChange-command: {} with  commandType: {}", productOrderDTO, eventType);
            checkParameter(productOrderDTO, eventType);
            ProductOrderCommand productOrderCommand = getProductOrderCommand(productOrderDTO, eventType);
            String partitionKey = productOrderDTO.getId();

            Message<ProductOrderCommand> message = MessageBuilder.withPayload(productOrderCommand)
                    .setHeader(PARTITION_KEY, partitionKey)
                    .build();

            streamBridge.send(PRODUCT_ORDER_COMMAND, message);

        } catch (Exception e) {
            log.error("Error publishing command message", e);
            throw e;
        }
    }
}