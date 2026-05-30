// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;


import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.BaseAbstractionIntegrationTest;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.builder.OrchestrationPlanBuilder.getOrchestrationPlanBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ListOrchestrationPlan extends BaseAbstractionIntegrationTest {

    @Autowired
    protected WireMockServer wireMockServer;

    protected MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MongoTemplate mongoTemplate;

    List<OrchestrationPlan> actualOrchestrationPlanList;

    OrchestrationPlan orchestrationPlan;

    @Before("@BeforeListOrchestrationPlan")
    @SneakyThrows
    @Override
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        wireMockServer.resetMappings();
        wireMockServer.resetRequests();
        wireMockServer.resetAll();
    }

    @Given("an orchestration plan has been created")
    public void anOrchestrationPlanHasBeenCreated() {
        orchestrationPlan = getOrchestrationPlanBuilder()
                .receivedDate(LocalDateTime.of(2023, Month.JUNE, 21, 8, 16, 2).toInstant(ZoneOffset.UTC)).build();
        mongoTemplate.save(orchestrationPlan);
    }

    @And("the logged in user has the appropriate role")
    public void theLoggedInUserHasTheAppropriateRole() {
        // the logged in user has the appropriate role
    }

    @When("the COOD administrator clicks to view the list of plans")
    public void theCOODAdministratorClicksToViewTheListOfPlans() throws Exception {
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get("/orchestrationPlan")
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        actualOrchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });
    }

    @Then("they should be able to view any datetime field with zone")
    public void theyShouldBeAbleToViewAnyDatetimeFieldInGMT() {
        assertEquals("2023-06-21T08:16:02Z",
                actualOrchestrationPlanList.stream().filter(node-> node.getId().equals(orchestrationPlan.getId())).findFirst().get().getReceivedDate().toString());
    }


}
