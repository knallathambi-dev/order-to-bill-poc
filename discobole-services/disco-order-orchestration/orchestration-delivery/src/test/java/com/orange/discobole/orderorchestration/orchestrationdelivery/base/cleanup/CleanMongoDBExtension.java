// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.mongodb.core.MongoTemplate;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.base.debezium.DebeziumAndKafkaIntegrationTestInitializer.mongoDbReplicaSet;


@TestComponent
public class CleanMongoDBExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        new MongoTemplate(mongo(), "COOD").dropCollection("orchestrationPlan");
    }

    public static MongoClient mongo() {
        ConnectionString connectionString = new ConnectionString(mongoDbReplicaSet.getConnectionString());
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

}
