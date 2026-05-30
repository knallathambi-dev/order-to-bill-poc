// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.metrics.GlobalMetricRegistry;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.tracing.SpanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@Configuration
public class AxonConfig {

    @Value("${spring.data.mongodb.host}")
private String mongoHost;

    @Value("${spring.data.mongodb.port}")
private int mongoPort;

    @Value("${spring.data.mongodb.database}")
private String mongoDatabase;

    @Value("${spring.data.mongodb.username}")
private String mongoUser;

    @Value("${spring.data.mongodb.password}")
private String mongoPassword;

    private MongoDatabaseFactory mongoDatabaseFactory;
//	@Autowired
//	SpanFactory spanFactory;

    @Bean
    public MongoClient mongo() {
        final ConnectionString connectionString = new ConnectionString("mongodb://" + mongoUser + ":" + mongoPassword
                + "@" + mongoHost + ":" + mongoPort + "/" + mongoDatabase);
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString).build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate axonMongoTemplate() {
        return DefaultMongoTemplate.builder().mongoDatabase(mongo(), mongoDatabase).build();
    }

    @Bean
    public TokenStore tokenStore(Serializer serializer) {
        return MongoTokenStore.builder().mongoTemplate(axonMongoTemplate()).serializer(JacksonSerializer.defaultSerializer()).build();
    }


    @Bean
    public EventStore eventStore(EventStorageEngine storageEngine,
                                 GlobalMetricRegistry metricRegistry, SpanFactory spanFactory) {

        return EmbeddedEventStore.builder()
                .storageEngine(storageEngine)
                .messageMonitor(metricRegistry.registerEventBus("eventStore"))
                .spanFactory(spanFactory)
                .build();
    }

    // The MongoEventStorageEngine stores each event in a separate MongoDB document.
    @Bean("eventStorageEngine")
    public EventStorageEngine storageEngine(MongoDatabaseFactory factory,
                                            TransactionManager transactionManager) {
        return MongoEventStorageEngine.builder()
                .mongoTemplate(SpringMongoTemplate.builder()
                        .factory(mongoDatabaseFactory())
                        .build())
                .transactionManager(transactionManager)
                .eventSerializer(JacksonSerializer.defaultSerializer())
                .snapshotSerializer(JacksonSerializer.defaultSerializer())
                .build();
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(mongo(), mongoDatabase);
    }

    @Bean
    public Supplier<Flux<String>> output() {
        return () -> {
            return Flux.just("OK");
        };
    }

}
