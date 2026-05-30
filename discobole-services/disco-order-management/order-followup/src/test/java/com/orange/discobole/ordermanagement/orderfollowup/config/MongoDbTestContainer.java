// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.Collections;
import java.util.Optional;

public class MongoDbTestContainer implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(MongoDbTestContainer.class);
    private static final int MONGO_PORT = 27017;
    private static final String MONGO_IMAGE = "mongo:4.4.15";
    private static final String INIT_SCRIPT_PATH = "./init-schema.js";
    private static final String CONTAINER_INIT_PATH = "/docker-entrypoint-initdb.d/init-script.js";
    private static final String TEST_MONGO_USERNAME = "TEST_MONGO_USERNAME";
    private static final String TEST_MONGO_PASSWORD = "TEST_MONGO_PASSWORD";

    private GenericContainer<?> mongodbContainer;

    @Override
    public void destroy() {
        if (isContainerRunning()) {
            mongodbContainer.stop();
            log.info("MongoDB Test Container stopped.");
        }
    }

    @Override
    public void afterPropertiesSet() {
        initializeMongoContainer();
        startContainerIfNeeded();
    }

    private void initializeMongoContainer() {
        if (mongodbContainer == null) {
            DockerImageName dockerImageName = DockerImageName.parse(TestConfigurationProperties.getDockerProxyIfExists() + MONGO_IMAGE)
                    .asCompatibleSubstituteFor("mongo");

            mongodbContainer = new GenericContainer<>(dockerImageName)
                    .withTmpFs(Collections.singletonMap("/testtmpfs", "rw"))
                    .withLogConsumer(new Slf4jLogConsumer(log))
                    .withReuse(true);
        }
    }

    private void startContainerIfNeeded() {
        if (!isContainerRunning()) {
            configureContainerEnvironment();
            copyInitScript();
            exposePortsAndStart();
        }
    }

    private void configureContainerEnvironment() {
        Optional.ofNullable(System.getenv(TEST_MONGO_USERNAME))
                .ifPresent(user -> mongodbContainer.addEnv(TEST_MONGO_USERNAME, user));
        Optional.ofNullable(System.getenv(TEST_MONGO_PASSWORD))
                .ifPresent(password -> mongodbContainer.addEnv(TEST_MONGO_PASSWORD, password));
    }

    private void copyInitScript() {
        mongodbContainer.withCopyFileToContainer(
                MountableFile.forClasspathResource(INIT_SCRIPT_PATH),
                CONTAINER_INIT_PATH
        );
    }

    private void exposePortsAndStart() {
        mongodbContainer.withExposedPorts(MONGO_PORT);
        mongodbContainer.start();
        log.info("MongoDB Test Container started on port {}", mongodbContainer.getMappedPort(MONGO_PORT));
    }

    private boolean isContainerRunning() {
        return mongodbContainer != null && mongodbContainer.isRunning();
    }

    public GenericContainer<?> getMongoDBContainer() {
        return mongodbContainer;
    }
}