// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.cucumber.tests;

import base.testutil.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class PersistFalloutIncident {

    protected MockMvc mvc;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected MongoTemplate mongoTemplate;

    private ProcessFlow processFlow;


    @Before
    @SneakyThrows
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        TimeUnit.SECONDS.sleep(2);
        mongoTemplate.findAllAndRemove(new Query(), FalloutIncident.class);
    }

    @Given("the request body with the following json {string}")
    public void the_request_body_with_the_following_json(String jsonFilePath) {
        this.processFlow = JsonUtil.readObjectFromResource(String.format("/integration/%s", jsonFilePath), new TypeReference<>() {
        });
    }

    @When("I send a POST request to create processManagement url")
    public void iSendAPOSTRequestToCreateProcessManagementUrl() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/processManagement/v1/processFlow")
                        .content(JsonUtil.toJsonStringFromObject(processFlow))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
    }

    @Then("the Fallout object should be persisted in the database with the following state {string}")
    public void theFalloutObjectShouldBePersistedInTheDatabaseWithTheFollowingState(String expectedState) {
        FalloutIncident fallout = mongoTemplate.findAll(FalloutIncident.class).get(0);
        Assert.assertEquals(expectedState, fallout.getState().getValue());
    }
}
