// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout;

import base.debezium.EnableDebeziumIntegration;
import io.cucumber.java.DataTableType;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import java.util.List;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.orange.discobole.orderorchestration.orchestrationdelivery.fallout," +
"com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.cucumber.tests" )
@CucumberContextConfiguration
@ComponentScan(basePackages = {"com.orange.discobole.orderorchestration.orchestrationdelivery.fallout", "com.orange.discobole.orderorchestration.orchestrationdelivery", "com.orange.disco.orderorchestration"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Profile("test")
@Slf4j
@EnableDebeziumIntegration
@CucumberOptions(features = "src/test/resources/features", glue = "com.orange.discobole.orderorchestration.orchestrationdelivery.fallout")
class CucumberTest {
    @DataTableType
    public String[] stringArray(List<String> dataTable) {
        return dataTable.toArray(new String[0]);
    }

}
