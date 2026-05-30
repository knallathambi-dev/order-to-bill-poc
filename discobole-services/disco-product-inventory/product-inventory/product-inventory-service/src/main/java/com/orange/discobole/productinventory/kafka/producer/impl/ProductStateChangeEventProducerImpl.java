// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.kafka.producer.impl;


import com.orange.discobole.productinventory.constant.Constant;
import com.orange.discobole.productinventory.dto.kafka.ProductStateChangeEvent;
import com.orange.discobole.productinventory.dto.kafka.ProductStateChangeEventPayload;
import com.orange.discobole.productinventory.dto.kafka.StateChangeProduct;
import com.orange.discobole.productinventory.enumerate.EventType;
import com.orange.discobole.productinventory.exception.model.BusinessErrors;
import com.orange.discobole.productinventory.kafka.producer.EventProducer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@Slf4j
public class ProductStateChangeEventProducerImpl implements EventProducer<StateChangeProduct> {

    private final KafkaTemplate<String, ProductStateChangeEvent> kafkaTemplate;


    private ProductStateChangeEvent createProductStateChangeEvent(StateChangeProduct product, String title, String domain) {
        ProductStateChangeEventPayload productStateChangeEventPayload =
                ProductStateChangeEventPayload
                        .builder()
                        .product(product)
                        .build();

        return ProductStateChangeEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(EventType.PRODUCT_STATE_CHANGE_EVENT.getValue())
                .event(productStateChangeEventPayload)
                .title(title)
                .domain(domain)
                .build();
    }

    private void validateProduct(StateChangeProduct product) {
        Assert.notNull(product, BusinessErrors.PRODUCT_MUST_NOT_BE_NULL);
        Assert.notNull(product.getId(), BusinessErrors.PRODUCT_ID_MUST_NOT_BE_NULL);
        Assert.notNull(product.getAtType(), BusinessErrors.PRODUCT_TYPE_MUST_NOT_BE_NULL);
        Assert.notNull(product.getStatus(), BusinessErrors.PRODUCT_STATUS_MUST_NOT_BE_NULL);
    }

    @Override
    public void publishEvent(StateChangeProduct product, String title, String domain) {
        validateProduct(product);
        ProductStateChangeEvent productStateChangeEvent = createProductStateChangeEvent(product, title, domain);
        String partitionKey = product.getId();
        Message<ProductStateChangeEvent> message = MessageBuilder.withPayload(productStateChangeEvent).build();
        kafkaTemplate.send(Constant.PRODUCT_STATE_CHANGE_EVENT_TOPIC, partitionKey, message.getPayload()).thenAccept(sendResult ->
                log.info("{}  is published on topic {} for partition {}",
                        "ProductStateChangeEvent",
                        partitionKey,
                        Constant.PRODUCT_STATE_CHANGE_EVENT_TOPIC)
        );
    }

    @Override
    public void publishEvents(Set<StateChangeProduct> products, String title, String domain) {
        for (StateChangeProduct product : products) {
            publishEvent(product, title, domain);
        }
    }


}
