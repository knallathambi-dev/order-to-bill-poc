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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsCommandListener;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsConnectionPoolListener;
import lombok.RequiredArgsConstructor;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.metrics.GlobalMetricRegistry;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.tracing.SpanFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Custom Axon configuration that overrides the default Process Flow library setup.
 * <p>
 * This configuration ensures that:
 * <ul>
 *   <li>The MongoDB client is created with metrics enabled for both command and connection pool monitoring.</li>
 *   <li>The MongoDB connection uses a URI-based {@link ConnectionString} instead of manually concatenating
 *       the username, password, and database name.</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class AxonConfig {

    private final MongoProperties mongoProperties;
    private final MeterRegistry meterRegistry;

    @Bean
    public ConnectionString connectionString() {
        return new ConnectionString(mongoProperties.getUri());
    }

    @Bean
    public MongoClient mongoClient(ConnectionString connectionString) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .addCommandListener(new MongoMetricsCommandListener(meterRegistry))
                .applyToConnectionPoolSettings(builder ->
                        builder.addConnectionPoolListener(new MongoMetricsConnectionPoolListener(meterRegistry)))
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient, ConnectionString connectionString) {
        String dbName = Optional.ofNullable(connectionString.getDatabase())
                .orElseThrow(() -> new IllegalStateException("Database name must be specified in MongoDB URI"));
        return new SimpleMongoClientDatabaseFactory(mongoClient, dbName);
    }

    @Bean
    public DefaultMongoTemplate axonMongoTemplate(MongoClient mongoClient, ConnectionString connectionString) {
        String dbName = connectionString.getDatabase();
        return DefaultMongoTemplate.builder()
                .mongoDatabase(mongoClient, dbName)
                .build();
    }

    @Bean
    public TokenStore tokenStore(DefaultMongoTemplate axonMongoTemplate) {
        return MongoTokenStore.builder()
                .mongoTemplate(axonMongoTemplate)
                .serializer(JacksonSerializer.defaultSerializer())
                .build();
    }

    @Bean("eventStorageEngine")
    public EventStorageEngine storageEngine(MongoDatabaseFactory factory, TransactionManager transactionManager) {
        return MongoEventStorageEngine.builder()
                .mongoTemplate(SpringMongoTemplate.builder().factory(factory).build())
                .transactionManager(transactionManager)
                .eventSerializer(JacksonSerializer.defaultSerializer())
                .snapshotSerializer(JacksonSerializer.defaultSerializer())
                .build();
    }

    @Bean
    public EventStore eventStore(EventStorageEngine storageEngine,
                                 GlobalMetricRegistry metricRegistry,
                                 SpanFactory spanFactory) {
        return EmbeddedEventStore.builder()
                .storageEngine(storageEngine)
                .messageMonitor(metricRegistry.registerEventBus("eventStore"))
                .spanFactory(spanFactory)
                .build();
    }

    @Bean
    public Supplier<Flux<String>> output() {
        return () -> Flux.just("OK");
    }
}