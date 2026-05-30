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
//import java.util.HashMap;
//import java.util.Map;
//
//
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
///**
// * Configurations for Kafka Producer.
// *
// * @author Saurabh Shakya
// * @since 1.0
// */
//@Configuration
//public class KafkaProducerConfig {
//
//	@Resource
//	private KafkaProperties kafkaProperties;
//
//	@Resource
//	private ObjectMapper objectMapper;
//
//	/**
//	 * This method sets the default Producer properties of Kafka.
//	 *
//	 * @return the producer factory
//	 */
//	@Bean
//	public ProducerFactory<String, Object> producerFactory() {
//		final Map<String, Object> props = new HashMap<>();
//		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
//		props.putAll(kafkaProperties.getProperties());
//		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//		props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
//		DefaultKafkaProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<>(props);
//		producerFactory.setValueSerializer(new JsonSerializer<>(objectMapper));
//		return producerFactory;
//	}
//
//	/**
//	 * Kafka template.
//	 *
//	 * @return the kafka template
//	 */
//	@Bean
//	public KafkaTemplate<String, Object> kafkaTemplate() {
//		return new KafkaTemplate<>(producerFactory());
//	}
//
//}


