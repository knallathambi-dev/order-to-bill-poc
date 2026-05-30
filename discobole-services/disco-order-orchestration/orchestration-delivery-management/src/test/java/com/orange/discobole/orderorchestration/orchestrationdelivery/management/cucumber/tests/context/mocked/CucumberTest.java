// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.context.mocked;


import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.cleanup.CleanMongoDBExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.cleanup.ResetWireMockExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.debezium.EnableDebeziumIntegration;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/context/mocked")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = """
        com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.context.mocked,
        com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.steps
        """)
@CucumberContextConfiguration
@ComponentScan(basePackages = {"com.orange.discobole.orderorchestration.orchestrationdelivery", "com.orange.discobole.orderorchestration"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Profile("test")
@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class, ResetWireMockExtension.class})
@Slf4j
@MockBean(EventPublisher.class)
public class CucumberTest {
}
