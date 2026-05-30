// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;

public class MongoDbTestContainer implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(MongoDbTestContainer.class);
    private static final String MONGO_IMAGE = "mongo:4.4.15";
    private MongoDBContainer mongodbContainer;

    @Override
    public void destroy() {
        if (null != mongodbContainer && mongodbContainer.isRunning()) {
            mongodbContainer.stop();
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (null == mongodbContainer) {
            DockerImageName dockerImageName = DockerImageName.parse(TestConfigurationProperties.getDockerProxyIfExists() + MONGO_IMAGE)
                    .asCompatibleSubstituteFor("mongo");

            mongodbContainer =
                    new MongoDBContainer(dockerImageName)
                            .withTmpFs(Collections.singletonMap("/testtmpfs", "rw"))
                            .withLogConsumer(new Slf4jLogConsumer(log))
                            .withReuse(true);
        }
        if (!mongodbContainer.isRunning()) {
            mongodbContainer.start();
        }
    }

    public MongoDBContainer getMongoDBContainer() {
        return mongodbContainer;
    }
}