// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.integration;

import base.BaseAbstractionIntegrationTest;
import base.debezium.EnableDebeziumIntegration;
import base.testutil.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.Error;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.RestErrorCode.FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableDebeziumIntegration
class FalloutManagementApiTests extends BaseAbstractionIntegrationTest {

    private static final String FALLOUT_INCIDENT_JSON = "/integration/fallouts/FalloutIncident.json";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FalloutRepository falloutRepository;

    private FalloutIncident expectedFalloutIncident;

    @BeforeEach
    public void setup() {
        falloutRepository.deleteAll();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        this.expectedFalloutIncident = JsonUtil.readObjectFromResource(FALLOUT_INCIDENT_JSON, new TypeReference<>() {
        });

        this.expectedFalloutIncident = mongoTemplate.save(this.expectedFalloutIncident);

        FalloutIncident unexpectedFalloutIncident = JsonUtil.readObjectFromResource(FALLOUT_INCIDENT_JSON, new TypeReference<>() {
        });
        unexpectedFalloutIncident.setId("1312");

        mongoTemplate.save(unexpectedFalloutIncident);
    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentList_thenReturned() throws Exception {
        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("id", expectedFalloutIncident.getId());
        falloutFilter.add("state", expectedFalloutIncident.getState().toString());
        falloutFilter.add("relatedParty.id", expectedFalloutIncident.getRelatedParty().getId());
        falloutFilter.add("relatedParty.role", expectedFalloutIncident.getRelatedParty().getRole());
        falloutFilter.add("relatedParty.name", expectedFalloutIncident.getRelatedParty().getName());
        falloutFilter.add("relatedEntity.role", expectedFalloutIncident.getRelatedEntity().get(0).getRole().getValue());
        falloutFilter.add("relatedEntity.id", expectedFalloutIncident.getRelatedEntity().get(0).getId());
        falloutFilter.add("fields", "id%2Cstate%2CcreationDate%2CmodificationDate%2CparentRelatedEntity%2CrelatedEntity%2CrelatedParty");
        falloutFilter.add("limit", "10");
        falloutFilter.add("offset", "0");
        falloutFilter.add("modificationDate.lte", expectedFalloutIncident.getModificationDate().plusHours(1).toString());
        falloutFilter.add("modificationDate.gte", expectedFalloutIncident.getModificationDate().minusHours(1).toString());
        falloutFilter.add("creationDate.lte", "2024-08-11T12:24:58.394Z");
        falloutFilter.add("creationDate.gte", "2024-08-11T12:24:58.394Z");
        falloutFilter.add("sort", "-creationDate");


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 1;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListById_thenReturned() throws Exception {

        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("id", expectedFalloutIncident.getId());

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 1;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListByState_thenReturned() throws Exception {

        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("state", expectedFalloutIncident.getState().toString());


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 2;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListByRelatedParty_thenReturned() throws Exception {
        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("relatedParty.id", expectedFalloutIncident.getRelatedParty().getId());
        falloutFilter.add("relatedParty.role", expectedFalloutIncident.getRelatedParty().getRole());
        falloutFilter.add("relatedParty.name", expectedFalloutIncident.getRelatedParty().getName());


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 2;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListByRelatedEntity_thenReturned() throws Exception {
        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("relatedEntity.role", expectedFalloutIncident.getRelatedEntity().get(0).getRole().getValue());
        falloutFilter.add("relatedEntity.id", expectedFalloutIncident.getRelatedEntity().get(0).getId());

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 2;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListByFieldsAndPaging_thenReturned() throws Exception {
        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("fields", "id%2Cstate%2CcreationDate%2CmodificationDate%2CparentRelatedEntity%2CrelatedEntity%2CrelatedParty");
        falloutFilter.add("limit", "10");
        falloutFilter.add("offset", "0");
        falloutFilter.add("sort", "-creationDate");


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 2;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListByModificationDateStart_thenReturned() throws Exception {
        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("modificationDate.gte", expectedFalloutIncident.getModificationDate().minusHours(1).toString());


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 2;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListByModificationDateEnd_thenReturned() throws Exception {

        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("modificationDate.lte", expectedFalloutIncident.getModificationDate().plusHours(1).toString());


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 2;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListByCreationDateStart_thenReturned() throws Exception {
        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("creationDate.gte", "2024-08-11T12:24:58.394Z");


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 2;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListByCreationDateEnd_thenReturned() throws Exception {

        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("creationDate.lte", "2024-08-11T12:24:58.394Z");


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 2;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListByModificationDate_thenReturned() throws Exception {
        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("modificationDate.lte", expectedFalloutIncident.getModificationDate().plusHours(1).toString());
        falloutFilter.add("modificationDate.gte", expectedFalloutIncident.getModificationDate().minusHours(1).toString());


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 2;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListCreationDate_thenReturned() throws Exception {

        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("creationDate.lte", "2024-08-11T12:24:58.394Z");
        falloutFilter.add("creationDate.gte", "2024-08-11T12:24:58.394Z");


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        int expectedSize = 2;
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident> falloutIncidentList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedSize, falloutIncidentList.size());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);

        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();

    }

    @ParameterizedTest
    @CsvSource({
            "1001,Limit exceed max value.",
            "-1,Invalid limit value."
    })
    void givenFalloutIncident_whenFetchFalloutIncidentListByInvalidLimit_thenReturnedError(String limit, String message) throws Exception {
        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("limit", limit);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        Error error = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(error.getCode()).isEqualTo(String.valueOf(FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION.getErrorCode()));
        assertThat(error.getReason()).isEqualTo(FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION.getMessage());
        assertThat(error.getMessage()).isEqualTo(message);
    }

    @ParameterizedTest
    @CsvSource({
            "10,-10,Invalid offset value.",
            "10,10,Invalid Offset value.",
            "-10,-10,Limit and Offset can't be negative."
    })
    void givenFalloutIncident_whenFetchFalloutIncidentListByInvalidOffset_thenReturnedError(String limit, String offset, String message) throws Exception {
        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("limit", limit);
        falloutFilter.add("offset", offset);


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        Error error = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(error.getCode()).isEqualTo(String.valueOf(FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION.getErrorCode()));
        assertThat(error.getReason()).isEqualTo(FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION.getMessage());
        assertThat(error.getMessage()).isEqualTo(message);
    }

    @Test
    void givenFalloutIncident_whenFetchFalloutIncidentListByInvalidSortValue_thenReturnedError() throws Exception {
        MultiValueMap<String, String> falloutFilter = new LinkedMultiValueMap<>();
        falloutFilter.add("sort", "dummyField");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/falloutIncident")
                        .params(falloutFilter))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        Error error = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(error.getCode()).isEqualTo(String.valueOf(FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION.getErrorCode()));
        assertThat(error.getReason()).isEqualTo(FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION.getMessage());
        assertThat(error.getMessage()).isEqualTo("Invalid sort value.");
    }
}
