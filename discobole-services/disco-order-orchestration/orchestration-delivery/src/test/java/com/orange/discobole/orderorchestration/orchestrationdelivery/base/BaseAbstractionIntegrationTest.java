// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.base.debezium.DebeziumAndKafkaIntegrationTestInitializer.kafkaContainer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Profile({"test", "no-security"})
public abstract class BaseAbstractionIntegrationTest {

    @Autowired
    protected WireMockServer wireMockServer;

    @Autowired
    private WebApplicationContext context;

    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @SneakyThrows
    public static String readFileToString(String path) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(path);
        try {
            return resource.getContentAsString(Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void kafkaDeletion() {
        int exitedSuccessful = 0;
        if (kafkaContainer.isRunning()) {
            await().atMost(60, SECONDS).untilAsserted(() ->
                    {
                        var result = kafkaContainer.execInContainer("../../../bin/kafka-topics", "--bootstrap-server", "localhost:9092", "--delete", "--topic", "'.*'");
                        int exitCode = result.getExitCode();
                        try {
                            Assertions.assertEquals(exitedSuccessful, exitCode);
                        } catch (AssertionError e) {
                            System.err.println("Assertion failed but allowed: " + e.getMessage());
                        }
                    }
            );
        }
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    protected <T> T readJson(ResultActions resultActions, TypeReference<T> typeReference) {
        T result;

        try {
            String json = resultActions
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            result = objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @AfterEach
    public void cleanDB() {
        mongoTemplate.dropCollection(OrchestrationPlan.class);
    }
    
}
