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
import com.orange.discobole.productinventory.dto.kafka.ProductDeleteEvent;
import com.orange.discobole.productinventory.dto.kafka.ProductDeleteEventPayload;
import com.orange.discobole.productinventory.dto.v1.Product;
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
public class ProductDeleteEventProducerImpl implements EventProducer<Product> {

    private final KafkaTemplate<String, ProductDeleteEvent> kafkaTemplate;


    private ProductDeleteEvent createProductDeleteEvent(Product product, String title, String domain) {
        ProductDeleteEventPayload productDeleteEventPayload =
                ProductDeleteEventPayload
                        .builder()
                        .product(product)
                        .build();

        return ProductDeleteEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .title(title)
                .domain(domain)
                .eventType(EventType.PRODUCT_DELETE_EVENT.getValue())
                .event(productDeleteEventPayload)
                .build();
    }

    @Override
    public void publishEvent(Product product, String title, String domain) {
        validateProduct(product);
        ProductDeleteEvent productDeleteEvent = createProductDeleteEvent(product, title, domain);
        String partitionKey = product.getId();
        Message<ProductDeleteEvent> message = MessageBuilder.withPayload(productDeleteEvent).build();
        kafkaTemplate.send(Constant.PRODUCT_DELETE_EVENT_TOPIC, partitionKey, message.getPayload())
                .thenAccept(sendResult ->
                        log.info("{}  is published on topic {} for partition {}",
                                "ProductDeleteEvent",
                                partitionKey,
                                Constant.PRODUCT_DELETE_EVENT_TOPIC)
                );

    }

    private void validateProduct(Product product) {
        Assert.notNull(product, BusinessErrors.PRODUCT_MUST_NOT_BE_NULL);
        Assert.notNull(product.getId(), BusinessErrors.PRODUCT_ID_MUST_NOT_BE_NULL);
        Assert.notNull(product.getAtType(), BusinessErrors.PRODUCT_TYPE_MUST_NOT_BE_NULL);
    }


    @Override
    public void publishEvents(Set<Product> products, String title, String domain) {
        for (Product product : products) {
            publishEvent(product, title, domain);
        }

    }

}
