// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.base;


import com.orange.discobole.orderorchestration.orchestrationdelivery.management.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.debezium.DebeziumAndKafkaIntegrationTestInitializer.kafkaContainer;


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
    public static <T> KafkaConsumer<String, T> createKafkaConsumer(String topicName, String groupId, Class<T> classType, String offsetConfig) {
        KafkaConsumer<String, T> consumer = new KafkaConsumer<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaContainer.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetConfig),
                new StringDeserializer(),
                AbstractIntegrationUtil.jsonDeserializer(classType)
        );
        consumer.subscribe(List.of(topicName));
        return consumer;
    }

    @SuppressWarnings("resource")
    public static <T> KafkaProducer<String, T> createKafkaProducer() {
        return new KafkaProducer<>(ImmutableMap.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaContainer.getBootstrapServers()),
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    public static <T> ResponseEntity<T> sendPost(String url, Object requestBody, Class<T> classType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(JsonUtil.toJsonStringFromObject(requestBody), headers);
        return WebClient.create()
                .post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .toEntity(classType)
                .block(); // This is a blocking call to get the result immediately, adjust as needed
    }

}
