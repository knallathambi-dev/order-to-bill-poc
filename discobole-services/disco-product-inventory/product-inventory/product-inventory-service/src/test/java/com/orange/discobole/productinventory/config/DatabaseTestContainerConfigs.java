// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class DatabaseTestContainerConfigs {

    private static final MongoDBContainer mongoDBContainer;
    @Value("${spring.data.mongodb.database}")
    private static String dataBaseName;

    static {
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse(TestConfigurationProperties.getDockerProxyIfExists() + "mongo:6.0.4").asCompatibleSubstituteFor("mongo"));
    }

    public static void configure(ConfigurableApplicationContext configurableApplicationContext) {
        startContainer();
        configureDatabase(configurableApplicationContext);
    }

    private static void startContainer() {
        if (!mongoDBContainer.isRunning()) {
            mongoDBContainer.start();
        }
    }

    private static void configureDatabase(ConfigurableApplicationContext configurableApplicationContext) {
        String propertyDatasourceURL = "spring.data.mongodb.uri=" + mongoDBContainer.getReplicaSetUrl(dataBaseName);

        TestPropertyValues.of(propertyDatasourceURL).applyTo(configurableApplicationContext.getEnvironment());
    }
}
