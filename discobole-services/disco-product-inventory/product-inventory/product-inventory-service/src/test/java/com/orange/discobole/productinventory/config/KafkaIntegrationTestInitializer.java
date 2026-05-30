// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@Slf4j
public class KafkaIntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Container
    public static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse(TestConfigurationProperties.getDockerProxyIfExists() + "confluentinc/cp-kafka:7.5.1").asCompatibleSubstituteFor("confluentinc/cp-kafka"));

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        kafkaContainer.start();
        log.info("kafka connection: {}", kafkaContainer.getBootstrapServers());
        TestPropertyValues.of("spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers()).applyTo(applicationContext.getEnvironment());
    }
}
