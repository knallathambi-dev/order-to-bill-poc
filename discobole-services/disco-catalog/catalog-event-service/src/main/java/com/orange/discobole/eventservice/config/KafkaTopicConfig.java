// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

//package com.orange.bos.eventservice.config;
//
//import org.apache.kafka.clients.admin.AdminClientConfig;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.KafkaAdmin;
//
//import javax.annotation.Resource;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Configurations for Kafka Topic creation.
// *
// * @author Saurabh Shakya
// * @since 1.0
// */
//@Configuration
//public class KafkaTopicConfig {
//
//    @Resource
//    private KafkaProperties kafkaProperties;
//
//    @Value("${spring.kafka.replication-factor}")
//    private short replicationFactor;
//
//    /**
//     * Kafka admin.
//     *
//     * @return the configurations to kafka admin
//     */
//    @Bean
//    public KafkaAdmin kafkaAdmin() {
//        final Map<String, Object> configs = new HashMap<>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
//        configs.putAll(kafkaProperties.getProperties());
//        return new KafkaAdmin(configs);
//    }
//
//    /**
//     * Creates a new topic.
//     *
//     * @return the topic external-notification
//     */
//    @Bean
//    public NewTopic topicNotification() {
//        return new NewTopic("external-notification", 4, replicationFactor);
//    }
//
//}



