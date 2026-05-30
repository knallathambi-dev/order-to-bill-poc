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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.OrchestrationPlanRecord;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.context.WebApplicationContext;


@Slf4j
public class Pagination {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> actualOrchestrationPlans;

    private String actualHeaders;

    @Before("@BeforePagination")
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @When("the user requests plans for page {int} with limit {int}")
    public void getPlan(int pageNumber, int limit) throws Exception {
        int offset = pageNumber * limit - limit;

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get("/orchestrationPlan")
                        .param("offset", String.valueOf(offset))
                        .param("limit", String.valueOf(limit))
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isPartialContent());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        actualHeaders = resultActions.andReturn().getResponse().getHeader("Link");
        actualOrchestrationPlans = objectMapper.readValue(response, new TypeReference<>() {});
    }

    @Then("the system responds with the following orchestration plan(s)")
    public void checkPlans(List<OrchestrationPlanRecord> planRecords) {

        IntStream.range(0, planRecords.size())
                .forEach(i -> assertThat(planRecords.get(i).planId())
                        .isEqualTo(actualOrchestrationPlans.get(i).getId()));
    }

    @Then("the system responds with the following pagination details in the headers:")
    public void checkHeaders(String expectedHeaders){
        assertThat(actualHeaders).isEqualTo(expectedHeaders);
    }
}
