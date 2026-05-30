// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

public class KafkaTestContainer implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(KafkaTestContainer.class);
    private static final String KAFKA_IMAGE = "confluentinc/cp-kafka:7.2.1";
    private KafkaContainer kafkaContainer;

    @Override
    public void destroy() {
        if (null != kafkaContainer && kafkaContainer.isRunning()) {
            kafkaContainer.close();
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (null == kafkaContainer) {
            DockerImageName dockerImageName =
                    DockerImageName.parse(TestConfigurationProperties.getDockerProxyIfExists() + KAFKA_IMAGE)
                            .asCompatibleSubstituteFor("confluentinc/cp-kafka");

            kafkaContainer =
                    new KafkaContainer(dockerImageName)
                            .withLogConsumer(new Slf4jLogConsumer(log))
                            .withReuse(true);
        }
        if (!kafkaContainer.isRunning()) {
            kafkaContainer.start();
        }
    }

    public KafkaContainer getKafkaContainer() {
        return kafkaContainer;
    }
}