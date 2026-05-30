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
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
public class CloseFalloutIncident {

    protected MockMvc mvc;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected MongoTemplate mongoTemplate;

    private ProcessFlow processFlow;

    private ResultActions mvcResult;

    @Before
    @SneakyThrows
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        TimeUnit.SECONDS.sleep(2);
        mongoTemplate.findAllAndRemove(new Query(), FalloutIncident.class);
    }

    @Given("a fallout incident with status Held for the following json {string}")
    public void givenHeldFalloutIncident_whenCloseAsUnresolved_thenSaveIt(String postBodyJsonFilePath) throws Exception {
        this.processFlow = JsonUtil.readObjectFromResource(String.format("/integration/%s", postBodyJsonFilePath), new TypeReference<>() {
        });

        MvcResult mvcResult = mvc.perform(post("/processManagement/v1/processFlow")
                .content(JsonUtil.toJsonStringFromObject(processFlow)).contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        Assertions.assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
        this.processFlow = JsonUtil.readObjectFromString(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        log.info(String.valueOf(mongoTemplate.findAll(FalloutIncident.class)));
    }

    @And("the logged in user has the Fallout_Manager role")
    public void givenLoggedInUser_whenRoleIsFalloutManager_thenIsOk() {
        //Will be implemented when adding roles to this service
    }

    @And("the related orchestration node status is also Held")
    public void givenRelatedOrchestrationNodeStatusIsAlsoHeld_thenIsOk() {
        //ignore
    }

    @When("the COOD administrator attempts to close the fallout incident by patch json body {string}")
    public void closeFalloutIncident_whenCloseAsUnresolved_thenIsOk(String patchResolutionBodyJsonFilePath) throws Exception {
        ProcessFlow patchedProcessFlow = JsonUtil.readObjectFromResource(String.format("/integration/%s", patchResolutionBodyJsonFilePath), new TypeReference<>() {
        });
        this.mvcResult = mvc.perform(patch(this.processFlow.getLinks().getNextTaskstoBePerformed().get(0).getHref())
                        .content(JsonUtil.toJsonStringFromObject(patchedProcessFlow)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Then("the fallout incident status should remain unchanged {string}")
    public void closeFalloutIncident_afterCloseAsUnresolved_thenIsOk(String expectedStatus) {
        FalloutIncident fallout = mongoTemplate.findAll(FalloutIncident.class).get(0);
        assertEquals(expectedStatus, fallout.getState().getValue());
    }

    @Then("the system should display a generic error message indicating the issue")
    public void theSystemShouldDisplayAGenericErrorMessageIndicatingTheIssue() {
        assertEquals(HttpStatus.BAD_REQUEST.value(), this.mvcResult.andReturn().getResponse().getStatus());
    }

    @Then("the system should prompt the user to set a reason for closing the fallout incident")
    public void theSystemShouldPromptTheUserToSetAReasonForClosingTheFalloutIncident() {
        assertEquals(HttpStatus.BAD_REQUEST.value(), this.mvcResult.andReturn().getResponse().getStatus());
    }

    @And("a generic error occurs during the closure process")
    public void aGenericErrorOccursDuringTheClosureProcess() {
        //ignore
    }

    @And("the orchestration node status should remain unchanged")
    public void theOrchestrationNodeStatusShouldRemainUnchanged() {
        //ignore
    }

    @And("the user set the resolution state to {string}")
    public void theUserSetTheResolutionStateTo(String arg0) {
        //ignore
    }

    @And("they didn't set a reason for closing the fallout incident")
    public void theyDidnTSetAReasonForClosingTheFalloutIncident() {
        //ignore
    }
}
