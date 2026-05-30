// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.orange.discobole.productinventory.constant.Constant;
import com.orange.discobole.productinventory.dto.kafka.*;
import com.orange.discobole.productinventory.dto.v1.Product;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import static com.orange.discobole.productinventory.util.AbstractIntegrationUtil.createKafkaConsumer;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AsyncAssertionUtil extends AbstractTest {


    private static final Duration POLL_DURATION = Duration.ofMillis(2000000);

    public static void consumeAndAssertEqualityForAttributeChangeEvent(Product product) {
        KafkaConsumer<String, ProductAttributeValueChangeEvent> consumer = createKafkaConsumer(Constant.PRODUCT_ATTRIBUTE_VALUE_CHANGE_EVENT_TOPIC, getGroupId(), ProductAttributeValueChangeEvent.class);

        Product eventProduct = null;
        int maxRetries = 10;
        Duration pollDuration = Duration.ofSeconds(5);

        for (int i = 0; i < maxRetries && eventProduct == null; i++) {
            ConsumerRecords<String, ProductAttributeValueChangeEvent> records = consumer.poll(pollDuration);
            for (ConsumerRecord<String, ProductAttributeValueChangeEvent> consumerRecord : records) {
                ProductAttributeValueChangeEvent event = consumerRecord.value();
                if (event.getEvent().getProduct().getId().equals(product.getId())) {
                    eventProduct = event.getEvent().getProduct();
                    break;
                }
            }
        }

        assertNotNull(eventProduct, "No record found for the specified product ID: " + product.getId());
        ProductAssertionUtil.assertProductEqualityOnIdAndAtType(product, eventProduct);
        consumer.close();
    }


    public static void consumeAndAssertEqualityForAttributeChangeEvent(Set<Product> products) {
        KafkaConsumer<String, ProductAttributeValueChangeEvent> consumer = createKafkaConsumer(Constant.PRODUCT_ATTRIBUTE_VALUE_CHANGE_EVENT_TOPIC, getGroupId(), ProductAttributeValueChangeEvent.class);
        ConsumerRecords<String, ProductAttributeValueChangeEvent> consumerRecord = consumer.poll(POLL_DURATION);
        assertFalse(consumerRecord.isEmpty(), "No messages received from Kafka topic within timeout");
        consumerRecord.forEach(consumerRecordEntry -> products.stream()
                .filter(product -> product.getId().equals(consumerRecordEntry.value().getEvent().getProduct().getId()))
                .forEach(product -> ProductAssertionUtil.assertProductEqualityOnIdAndAtType(product, consumerRecordEntry.value().getEvent().getProduct())));
        consumer.close();
    }

    public static void consumeAndAssertEqualityForStateChangeEvent(StateChangeProduct product) {
        KafkaConsumer<String, ProductStateChangeEvent> consumer = createKafkaConsumer(Constant.PRODUCT_STATE_CHANGE_EVENT_TOPIC, getGroupId(), ProductStateChangeEvent.class);

        Product eventProduct = null;
        int maxRetries = 10;
        Duration pollDuration = Duration.ofSeconds(5);

        for (int i = 0; i < maxRetries && eventProduct == null; i++) {
            ConsumerRecords<String, ProductStateChangeEvent> records = consumer.poll(pollDuration);
            for (ConsumerRecord<String, ProductStateChangeEvent> consumerRecordEntry : records) {
                ProductStateChangeEvent event = consumerRecordEntry.value();
                if (event.getEvent().getProduct().getId().equals(product.getId())) {
                    eventProduct = event.getEvent().getProduct();
                    break;
                }
            }
        }

        assertNotNull(eventProduct, "No record found for the specified product ID: " + product.getId());
        ProductAssertionUtil.assertProductEqualityOnIdAndAtType(product, eventProduct);
        consumer.close();
    }

    public static void consumeAndAssertEqualityForStateChangeEvent(Set<StateChangeProduct> products) {
        KafkaConsumer<String, ProductStateChangeEvent> consumer = createKafkaConsumer(Constant.PRODUCT_STATE_CHANGE_EVENT_TOPIC, getGroupId(), ProductStateChangeEvent.class);
        ConsumerRecords<String, ProductStateChangeEvent> records = consumer.poll(POLL_DURATION);

        // Assertions
        assertFalse(records.isEmpty(), "No messages received from Kafka topic within timeout");


        records.forEach(productRecord -> products.stream()
                .filter(product -> product.getId().equals(productRecord.value().getEvent().getProduct().getId()))
                .forEach(product -> ProductAssertionUtil.assertProductEqualityOnIdAndAtType(product, productRecord.value().getEvent().getProduct())));
        consumer.close();
    }

    public static void consumeAndAssertEventReceivedProductOrderStateChange() {
        KafkaConsumer<String, ProductAttributeValueChangeEvent> consumer = createKafkaConsumer(Constant.DISCO_ORDER_MANAGEMENT_PRODUCT_ORDER_STATE_CHANGE_EVENT_TOPIC, getGroupId(), ProductAttributeValueChangeEvent.class);
        ConsumerRecords<String, ProductAttributeValueChangeEvent> records = consumer.poll(POLL_DURATION);

        // Assertions
        assertFalse(records.isEmpty(), "No messages received from Kafka topic within timeout");
        consumer.close();
    }


    public static void consumeAndAssertEqualityForDeleteProductEvent(Product product) {
        KafkaConsumer<String, ProductDeleteEvent> consumer = createKafkaConsumer(Constant.PRODUCT_DELETE_EVENT_TOPIC, getGroupId(), ProductDeleteEvent.class);

        Product eventProduct = null;
        int maxRetries = 10;
        Duration pollDuration = Duration.ofSeconds(5);

        for (int i = 0; i < maxRetries && eventProduct == null; i++) {
            ConsumerRecords<String, ProductDeleteEvent> records = consumer.poll(pollDuration);
            for (ConsumerRecord<String, ProductDeleteEvent> consumerRecord : records) {
                ProductDeleteEvent event = consumerRecord.value();
                if (event.getEvent().getProduct().getId().equals(product.getId())) {
                    eventProduct = event.getEvent().getProduct();
                    break;
                }
            }
        }

        assertNotNull(eventProduct, "No record found for the specified product ID: " + product.getId());
        ProductAssertionUtil.assertProductEqualityOnIdAndAtType(product, eventProduct);
        consumer.close();
    }


    public static void consumeAndAssertEqualityForCreateProductEvent(Product product) {
        KafkaConsumer<String, ProductCreateEvent> consumer = createKafkaConsumer(Constant.PRODUCT_CREATE_EVENT_TOPIC, getGroupId(), ProductCreateEvent.class);

        Product eventProduct = null;
        int maxRetries = 10;
        Duration pollDuration = Duration.ofSeconds(5);

        for (int i = 0; i < maxRetries && eventProduct == null; i++) {
            ConsumerRecords<String, ProductCreateEvent> records = consumer.poll(pollDuration);
            for (ConsumerRecord<String, ProductCreateEvent> consumerRecord : records) {
                ProductCreateEvent event = consumerRecord.value();
                if (event.getEvent().getProduct().getId().equals(product.getId())) {
                    eventProduct = event.getEvent().getProduct();
                    break;
                }
            }
        }

        assertNotNull(eventProduct, "No record found for the specified product ID: " + product.getId());
        ProductAssertionUtil.assertProductEqualityOnIdAndAtType(product, eventProduct);
        consumer.close();
    }

    private static @NotNull String getGroupId() {
        return "test-output-group-" + UUID.randomUUID();
    }
}

