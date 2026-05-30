// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package base.mongodb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.concurrent.TimeUnit;

@Testcontainers
@Slf4j
public class MongodbIntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Container

    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0.4")

            .asCompatibleSubstituteFor("mongo"))
            .withCopyFileToContainer(MountableFile.forClasspathResource("./init-script.js"), "/docker-entrypoint-initdb.d/init-script.js");

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            if (!(mongoDBContainer.isCreated() && mongoDBContainer.isRunning())) {
                mongoDBContainer.start();
                TimeUnit.MINUTES.sleep(1);
            }
            log.info("mongodb connection: {}", mongoDBContainer);
            TestPropertyValues.of("spring.data.mongodb.port=" + mongoDBContainer.getFirstMappedPort()).applyTo(applicationContext.getEnvironment());
            TestPropertyValues.of("spring.data.mongodb.host=" + mongoDBContainer.getHost()).applyTo(applicationContext.getEnvironment());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
