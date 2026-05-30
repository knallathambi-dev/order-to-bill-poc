// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package base.debezium;

import io.debezium.testing.testcontainers.ConnectorConfiguration;
import io.debezium.testing.testcontainers.DebeziumContainer;
import io.debezium.testing.testcontainers.MongoDbReplicaSet;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

@Testcontainers
public class DebeziumIntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Network network = Network.newNetwork();

    @Container
    public static final MongoDbReplicaSet mongoDbReplicaSet = MongoDbReplicaSet.replicaSet()
            .imageName(DockerImageName.parse("dockerproxy.repos.tech.orange/mongo:8.0.0")
                    .asCompatibleSubstituteFor("mongo"))
            .network(network)
            .memberCount(1)
            .authEnabled(true)
            .namespace("docker")
            .build();

    @Container
    public static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("dockerproxy.repos.tech.orange/confluentinc/cp-kafka:7.5.1")
            .asCompatibleSubstituteFor("confluentinc/cp-kafka"))
            .withNetwork(network);

    @Container
    public static final DebeziumContainer debeziumContainer =
            new DebeziumContainer("dockerproxy.repos.tech.orange/debezium/connect:2.6.1.Final")
                    .withNetwork(network)
                    .withKafka(kafkaContainer)
                    .dependsOn(mongoDbReplicaSet);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Startables.deepStart(Stream.of(kafkaContainer, mongoDbReplicaSet, debeziumContainer)).join();
        mongoDbReplicaSet.createUser("adminUser", "adminUser", "falloutManagement", "readWrite");
        ConnectorConfiguration connectorConfiguration = ConnectorConfiguration
                .forMongoDbReplicaSet(mongoDbReplicaSet)
                .with("tasks.max", 1)
                .with("topic.prefix", "dbserver1")
                .with("database.include.list", "falloutManagement")
                .with("collection.include.list", "falloutManagement.events")
                .with("transforms", "outbox")
                .with("transforms.outbox.type", "io.debezium.connector.mongodb.transforms.outbox.MongoEventRouter")
                .with("transforms.outbox.route.topic.replacement", "${routedByValue}")
                .with("transforms.outbox.collection.expand.json.payload", true)
                .with("transforms.outbox.collection.field.event.timestamp", "timestamp")
                .with("transforms.outbox.collection.fields.additional.placement", "type:header:eventType")
                .with("transforms.outbox.route.by.field", "type")
                .with("key.converter", "org.apache.kafka.connect.storage.StringConverter")
                .with("value.converter", "org.apache.kafka.connect.storage.StringConverter")
                .with("include.schema.changes", false)
                .with("errors.tolerance", "all")
                .with("errors.log.enable", true)
                .with("errors.log.include.messages", true);
        debeziumContainer.registerConnector("outbox-connector", connectorConfiguration);
        TestPropertyValues.of("spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers()).applyTo(applicationContext.getEnvironment());
        TestPropertyValues.of("spring.data.mongodb.uri=" + mongoDbReplicaSet.getConnectionString()).applyTo(applicationContext.getEnvironment());
        TestPropertyValues.of("spring.data.mongodb.port=" + mongoDbReplicaSet.tryPrimary().get().getClientAddress().getPort()).applyTo(applicationContext.getEnvironment());
    }
}
