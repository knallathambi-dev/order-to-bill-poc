// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.base.debezium;

import io.debezium.testing.testcontainers.ConnectorConfiguration;
import io.debezium.testing.testcontainers.DebeziumContainer;
import io.debezium.testing.testcontainers.MongoDbReplicaSet;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Objects;
import java.util.stream.Stream;

@Testcontainers
public class DebeziumAndKafkaIntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Network network = Network.newNetwork();

    public static String getImageByProxy(String image) {
        String dockerRegistryMirror = System.getenv("DOCKER_REGISTRY_MIRROR");
        if (Objects.isNull(dockerRegistryMirror)) {
            return image;
        }
        return dockerRegistryMirror + "/" + image;
    }

    @Container
    public static final MongoDbReplicaSet mongoDbReplicaSet = MongoDbReplicaSet.replicaSet()
            .network(network)
            .memberCount(1)
            .authEnabled(false)
            .namespace("docker")
            .imageName(DockerImageName.parse(getImageByProxy("mongo:8.0.0")))
            .build();

    @Container
    public static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse(getImageByProxy("confluentinc/cp-kafka:7.5.1"))
                    .asCompatibleSubstituteFor("confluentinc/cp-kafka")
    )
            .withNetwork(network)
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofMinutes(2));

    @Container
    public static final DebeziumContainer debeziumContainer = new DebeziumContainer(getImageByProxy("debezium/connect:2.6.1.Final"))
            .withNetwork(network)
            .withKafka(kafkaContainer)
            .dependsOn(mongoDbReplicaSet);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (!debeziumContainer.isRunning()) {
            Startables.deepStart(Stream.of(mongoDbReplicaSet, kafkaContainer, debeziumContainer)).join();
            ConnectorConfiguration connectorConfiguration = ConnectorConfiguration
                    .forMongoDbReplicaSet(mongoDbReplicaSet)
                    .with("tasks.max", 1)
                    .with("topic.prefix", "dbserver1")
                    .with("database.include.list", "COOD")
                    .with("collection.include.list", "COOD.events")
                    .with("transforms", "outbox")
                    .with("transforms.outbox.type", "io.debezium.connector.mongodb.transforms.outbox.MongoEventRouter")
                    .with("transforms.outbox.route.topic.replacement", "${routedByValue}")
                    .with("transforms.outbox.collection.expand.json.payload", true)
                    .with("transforms.outbox.collection.field.event.timestamp", "timestamp")
                    .with("transforms.outbox.collection.fields.additional.placement", "type:header:eventType,headers:header:headers,traceparent:header:traceparent")
                    .with("transforms.outbox.route.by.field", "type")
                    .with("key.converter", "org.apache.kafka.connect.storage.StringConverter")
                    .with("value.converter", "org.apache.kafka.connect.storage.StringConverter")
                    .with("include.schema.changes", false)
                    .with("errors.tolerance", "all")
                    .with("errors.log.enable", true)
                    .with("errors.log.include.messages", true);
            debeziumContainer.registerConnector("outbox-connector", connectorConfiguration);
        }
        TestPropertyValues.of("spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers()).applyTo(applicationContext.getEnvironment());
        TestPropertyValues.of("spring.data.mongodb.uri=" + mongoDbReplicaSet.getConnectionString()).applyTo(applicationContext.getEnvironment());
    }
}
