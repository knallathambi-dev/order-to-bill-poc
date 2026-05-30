// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.util.List;

import static com.orange.discobole.productinventory.config.KafkaIntegrationTestInitializer.kafkaContainer;


@Slf4j
public class AbstractIntegrationUtil {

    public static <T> JsonDeserializer<T> jsonDeserializer(Class<T> classType) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<T>(classType);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }

    @SuppressWarnings("resource")
    public static <T> KafkaConsumer<String, T> createKafkaConsumer(String topicName, String groupId, Class<T> classType) {
        KafkaConsumer<String, T> consumer = new KafkaConsumer<>(ImmutableMap.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaContainer.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"),
                new StringDeserializer(),
                AbstractIntegrationUtil.jsonDeserializer(classType)
        );
        consumer.subscribe(List.of(topicName));
        return consumer;
    }

}
