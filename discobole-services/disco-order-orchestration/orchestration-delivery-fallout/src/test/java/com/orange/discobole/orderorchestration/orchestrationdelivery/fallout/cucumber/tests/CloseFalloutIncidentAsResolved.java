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
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.pojo.enums.ResolutionState;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
public class CloseFalloutIncidentAsResolved {

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

    @Given("a fallout incident with status Held")
    public void givenHeldFalloutIncident_whenCloseAsUnresolved_thenSaveIt() throws Exception {
        this.processFlow = JsonUtil.readObjectFromResource("/integration/create/processFlowPostRequest.json", new TypeReference<>() {
        });

        MvcResult mvcResult = mvc.perform(post("/processManagement/v1/processFlow")
                .content(JsonUtil.toJsonStringFromObject(processFlow)).contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        this.processFlow = JsonUtil.readObjectFromString(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        log.info(String.valueOf(mongoTemplate.findAll(FalloutIncident.class)));
    }

    @When("the COOD administrator chooses to close the fallout incident with Resolved and reason patch json body {string}")
    public void theCOODAdministratorChoosesToCloseTheFalloutIncidentWithResolvedAndReasonPatchJsonBodyString(String patchResolutionBodyJsonFilePath) throws Exception {

        ProcessFlow patchedProcessFlow = JsonUtil.readObjectFromResource(String.format("/integration/%s", patchResolutionBodyJsonFilePath), new TypeReference<>() {
        });
        this.mvcResult = mvc.perform(patch(this.processFlow.getLinks().getNextTaskstoBePerformed().get(0).getHref())
                        .content(JsonUtil.toJsonStringFromObject(patchedProcessFlow)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));


    }

    @Then("after the fallout incident is closed, its status should be set to Completed")
    public void afterTheFalloutIncidentIsClosedItsStatusShouldBeSetToCompleted() {
        FalloutIncident fallout = mongoTemplate.findAll(FalloutIncident.class).get(0);
        assertEquals("Completed", fallout.getState().getValue());
        assertEquals(ResolutionState.RESOLVED, fallout.getResolution().getStatus());
        assertNotNull(fallout.getResolution().getComment());

    }

    @And("the fallout incident status should remain unchanged")
    public void theFalloutIncidentStatusShouldRemainUnchanged() {
        FalloutIncident fallout = mongoTemplate.findAll(FalloutIncident.class).get(0);
        assertEquals("Held", fallout.getState().getValue());
    }

    @Given("a fallout incident with status {string}")
    public void aFalloutIncidentWithStatus(String status) throws Exception {

        this.processFlow = JsonUtil.readObjectFromResource("/integration/create/processFlowPostRequest.json", new TypeReference<>() {
        });

        MvcResult mvcResult = mvc.perform(post("/processManagement/v1/processFlow")
                .content(JsonUtil.toJsonStringFromObject(processFlow)).contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        this.processFlow = JsonUtil.readObjectFromString(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        log.info(String.valueOf(mongoTemplate.findAll(FalloutIncident.class)));
        FalloutIncident fallout = mongoTemplate.findAll(FalloutIncident.class).get(0);
        fallout.setState(State.fromValue(status));
        mongoTemplate.save(fallout);
    }

    @And("an error occurs during the closure process")
    public void anErrorOccursDuringTheClosureProcess() {
        //ignore
    }

    @And("the related orchestration node status is also {string}")
    public void theRelatedOrchestrationNodeStatusIsAlso(String status) {
        log.debug(status);
        //ignore
    }


    @Then("the system should send an error message indicating that the incident cant be closed because its related nodes current status is different than Held")
    public void theSystemShouldSendAnErrorMessageIndicatingThatTheIncidentCantBeClosedBecauseItsRelatedNodesCurrentStatusIsDifferentThanHeld() {
        //ignore

    }

    @Then("the system should prompt the user to set a reason for closing the fallout incident as resolved")
    public void theSystemShouldPromptTheUserToSetAReasonForClosingTheFalloutIncidentAsResolved() {
        //ignorew
    }

    @Then("the system should display a generic error message indicating the issue for resolved incident")
    public void theSystemShouldDisplayAGenericErrorMessageIndicatingTheIssueForResolvedIncident() {
        assertEquals(HttpStatus.BAD_REQUEST.value(), this.mvcResult.andReturn().getResponse().getStatus());

    }
}
