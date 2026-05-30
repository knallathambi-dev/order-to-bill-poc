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
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.testcontainers.containers.KafkaContainer;

import java.util.List;

public class TestContainersSpringContextCustomizerFactory implements ContextCustomizerFactory {

    private static KafkaTestContainer kafkaBean;
    private static MongoDbTestContainer mongoDbBean;
    private static final int MONGO_PORT = 27017;
    private final Logger log = LoggerFactory.getLogger(TestContainersSpringContextCustomizerFactory.class);

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
        return (context, mergedConfig) -> {
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            TestPropertyValues testValues = TestPropertyValues.empty();
            EmbeddedMongo mongoAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedMongo.class);

            if (null != mongoAnnotation) {
                log.debug("Detected the EmbeddedMongo annotation on class {}", testClass.getName());
                log.info("Warming up the mongo database");
                if (null == mongoDbBean) {
                    mongoDbBean = beanFactory.createBean(MongoDbTestContainer.class);
                    beanFactory.registerSingleton(MongoDbTestContainer.class.getName(), mongoDbBean);
                }
                testValues = testValues.and("spring.data.mongodb.uri=" + mongoDbBean.getMongoDBContainer());
                testValues = testValues.and("spring.data.mongodb.host=" + mongoDbBean.getMongoDBContainer().getHost());
                testValues = testValues.and("spring.data.mongodb.port=" + mongoDbBean.getMongoDBContainer().getMappedPort(MONGO_PORT));
            }

            EmbeddedKafka kafkaAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedKafka.class);
            if (null != kafkaAnnotation) {
                log.debug("Detected the EmbeddedKafka annotation on class {}", testClass.getName());
                log.info("Warming up the kafka broker");
                if (null == kafkaBean) {
                    kafkaBean = beanFactory.createBean(KafkaTestContainer.class);
                    beanFactory.registerSingleton(KafkaTestContainer.class.getName(), kafkaBean);
                }
                testValues =
                        testValues.and(
                                "spring.cloud.stream.kafka.binder.brokers=" +
                                        kafkaBean.getKafkaContainer().getHost() +
                                        ':' +
                                        kafkaBean.getKafkaContainer().getMappedPort(KafkaContainer.KAFKA_PORT)
                        );
            }
            testValues.applyTo(context);
        };
    }
}