// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.LeadTimeStatisticsRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.dto.v1.ContractLeadTimeHistoryStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.dto.v1.LeadTimeHistorySample;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.dto.v1.NodeLeadTimeHistoryStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.ContractLeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.NodeLeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics.ContractLeadTimeHistoryStatisticsRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics.NodeLeadTimeHistoryStatisticsRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class LeadTimeHistoryStatisticsRest {

    @Autowired
    private ContractLeadTimeHistoryStatisticsRepository contractLeadTimeHistoryStatisticsRepository;

    @Autowired
    private NodeLeadTimeHistoryStatisticsRepository nodeLeadTimeHistoryStatisticsRepository;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult mvcResult;

    List<LeadTimeHistorySample> leadTimeHistorySamples;

    protected MockMvc mvc;

    @Before
    public void beforeEach() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        mvcResult = null;
        leadTimeHistorySamples = null;
    }

    @Given("the system has the following contract lead time history statistics")
    public void theSystemHasTheFollowingContractLeadTimeHistoryStatistics(List<LeadTimeStatisticsRecord> rows) {
        List<ContractLeadTimeHistorySampledStatistics> docs = rows.stream()
                .map(r -> ContractLeadTimeHistorySampledStatistics.builder()
                        .contractName(r.contractName())
                        .minActualLeadTime(r.minActualLeadTime())
                        .maxActualLeadTime(r.maxActualLeadTime())
                        .averageActualLeadTime((float) r.averageActualLeadTime())
                        .sampleSize(r.sampleSize())
                        .sampleWindow(r.sampleWindow())
                        .build())
                .collect(Collectors.toCollection(java.util.ArrayList::new));

        contractLeadTimeHistoryStatisticsRepository.saveAll(docs);
    }


    @When("the user requests contract lead time history statistics with query params {string}")
    public void theUserRequestsContractLeadTimeHistoryStatisticsWithQueryParams(String queryParams) throws Exception {
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get("/leadTimeHistoryStatistics/contract" + queryParams)
                .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200"))))
                .contentType(MediaType.APPLICATION_JSON)
        );

        mvcResult = resultActions.andReturn();
    }

    @Then("the system responds with status code {int} contract name {string}")
    public void theSystemRespondsWithStatusCodeContractName(
            int statusCode,
            String contractName
    ) throws UnsupportedEncodingException, JsonProcessingException {
        ContractLeadTimeHistoryStatistics contractLeadTimeHistoryStatisticsResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ContractLeadTimeHistoryStatistics.class
        );

        leadTimeHistorySamples = contractLeadTimeHistoryStatisticsResponse.getStatistics();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(statusCode);
        assertThat(contractLeadTimeHistoryStatisticsResponse.getContractName()).isEqualTo(contractName);
    }

    @And("the following lead time history statistics")
    public void theFollowingLeadTimeHistoryStatistics(List<LeadTimeStatisticsRecord> expectedRows) {
        assertThat(leadTimeHistorySamples).hasSameSizeAs(expectedRows);
        for (int i = 0; i < expectedRows.size(); i++) {
            var expectedRow = expectedRows.get(i);
            var actualRow = leadTimeHistorySamples.get(i);
            assertThat(actualRow.getSampleSize()).isEqualTo(expectedRow.sampleSize());
            assertThat(actualRow.getSampleWindow().toInstant()).isEqualTo(expectedRow.sampleWindow());
            assertThat(actualRow.getMinActualLeadTime()).isEqualTo(expectedRow.minActualLeadTime());
            assertThat(actualRow.getMaxActualLeadTime()).isEqualTo(expectedRow.maxActualLeadTime());
            assertThat(actualRow.getAverageActualLeadTime()).isCloseTo((float) expectedRow.averageActualLeadTime(), within(0.01f));
        }
    }

    @And("the statistics are sorted by sample window {string}")
    public void theStatisticsAreSortedBySampleWindowAscending(String sortCriteria) {
        OffsetDateTime lastDate = null;

        for (var statistic : leadTimeHistorySamples) {
            if (lastDate != null) {
                if ("ascending".equals(sortCriteria)) {
                    assertThat(statistic.getSampleWindow()).isAfterOrEqualTo(lastDate);
                } else {
                    assertThat(statistic.getSampleWindow()).isBeforeOrEqualTo(lastDate);
                }
            }
            lastDate = statistic.getSampleWindow();
        }
    }

    @And("the response headers contain {string} = {string}")
    public void theResponseHeadersContain(String headerKey, String headerValue) {
        assertThat(mvcResult.getResponse().getHeader(headerKey)).isEqualTo(headerValue);
    }

    @Given("the system has the following node lead time history statistics")
    public void theSystemHasTheFollowingNodeLeadTimeHistoryStatistics(List<LeadTimeStatisticsRecord> rows) {
        List<NodeLeadTimeHistorySampledStatistics> docs = rows.stream()
                .map(r -> NodeLeadTimeHistorySampledStatistics.builder()
                        .productSpecId(r.productSpecId())
                        .deliveryFactoryName(r.deliveryFactoryName())
                        .minActualLeadTime(r.minActualLeadTime())
                        .maxActualLeadTime(r.maxActualLeadTime())
                        .averageActualLeadTime((float) r.averageActualLeadTime())
                        .sampleSize(r.sampleSize())
                        .sampleWindow(r.sampleWindow())
                        .build())
                .collect(Collectors.toCollection(java.util.ArrayList::new));

        nodeLeadTimeHistoryStatisticsRepository.saveAll(docs);
    }

    @When("the user requests node lead time history statistics with query params {string}")
    public void theUserRequestsNodeLeadTimeHistoryStatisticsWithQueryParams(String queryParams) throws Exception {
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get("/leadTimeHistoryStatistics/node" + queryParams)
                .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200"))))
                .contentType(MediaType.APPLICATION_JSON)
        );

        mvcResult = resultActions.andReturn();
    }

    @Then("the system responds with status code {int} product spec id {string}")
    public void theSystemRespondsWithStatusCodeProductSpecId(
            int statusCode,
            String specId
    ) throws UnsupportedEncodingException, JsonProcessingException {
        NodeLeadTimeHistoryStatistics nodeLeadTimeHistoryStatistics = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                NodeLeadTimeHistoryStatistics.class
        );

        leadTimeHistorySamples = nodeLeadTimeHistoryStatistics.getStatistics();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(statusCode);
        assertThat(nodeLeadTimeHistoryStatistics.getProductSpecId()).isEqualTo(specId);
    }

    @Then("the system responds with status code {int} delivery factory name {string}")
    public void theSystemRespondsWithStatusCodeDeliveryFactoryName(int statusCode, String deliveryFactoryName) throws UnsupportedEncodingException, JsonProcessingException {
        NodeLeadTimeHistoryStatistics nodeLeadTimeHistoryStatistics = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                NodeLeadTimeHistoryStatistics.class
        );

        leadTimeHistorySamples = nodeLeadTimeHistoryStatistics.getStatistics();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(statusCode);
        assertThat(nodeLeadTimeHistoryStatistics.getDeliveryFactoryName()).isEqualTo(deliveryFactoryName);
        assertNull(nodeLeadTimeHistoryStatistics.getProductSpecId());
    }
}
