// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.integration.orchestrationplanapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.BaseAbstractionIntegrationTest;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.CleanMongoDBExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.ResetWireMockExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.debezium.EnableDebeziumIntegration;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Error;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlanNodeStateEnum;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlanStateEnum;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.orange.discobole.orderorchestration.exception.model.constants.RestErrorCode.COOD_VALIDATION_INVALID_QUERY_EXCEPTION;
import static java.util.Collections.reverseOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class, ResetWireMockExtension.class})
class FetchOrchestrationPlanTests extends BaseAbstractionIntegrationTest {

    private static final String ORCHESTRATION_PLAN_JSON = "/integration/OrchestrationPlanAfterBuild.json";
    private static final String X_TOTAL_COUNT_HEADER = "X-Total-Count";
    private static final String X_RESULT_COUNT_HEADER = "X-Result-Count";
    private static final String LINK_HEADER = "Link";
    private static final String ORCHESTRATION_PLAN_API_URL = "/orchestrationPlan";
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private OrchestrationPlanMapper orchestrationPlanMapper;

    private static void assertHeaders(ResultActions resultActions, int expectedXTotalCountHeader, int expectedXResultCountHeader) {
        Collection<String> actualHeaderNames = resultActions.andReturn().getResponse().getHeaderNames();
        assertThat(actualHeaderNames).containsAnyOf(X_RESULT_COUNT_HEADER, X_TOTAL_COUNT_HEADER, LINK_HEADER);
        assertNotNull(resultActions.andReturn().getResponse().getHeader(X_RESULT_COUNT_HEADER));
        assertEquals(String.valueOf(expectedXResultCountHeader), resultActions.andReturn().getResponse().getHeader(X_RESULT_COUNT_HEADER));
        assertNotNull(resultActions.andReturn().getResponse().getHeader(X_TOTAL_COUNT_HEADER));
        assertEquals(String.valueOf(expectedXTotalCountHeader), resultActions.andReturn().getResponse().getHeader(X_TOTAL_COUNT_HEADER));
        assertNotNull(resultActions.andReturn().getResponse().getHeader(LINK_HEADER));
    }

    @Test
    void givenSavedOrchestrationPlan_whenFetchOrchestrationPlans_thenReturnedSaved() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });

        mongoTemplate.save(expectedOrchestrationPlan);


        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL + "?orchestrationPlanNodes.state=Initialized")
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> orchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });

        assertEquals(List.of(expectedOrchestrationPlan).size(), orchestrationPlanList.size());
        assertThat(orchestrationPlanMapper.toDto(expectedOrchestrationPlan))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan.Fields.href)
                .withComparatorForType(
                        Comparator.comparing(a -> a.truncatedTo(ChronoUnit.MILLIS)),
                        LocalDateTime.class
                )
                .isEqualTo(orchestrationPlanList.get(0));
        assertHeaders(resultActions, 1, 1);
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchOrchestrationPlanById_thenReturnedOrchestrationPlanWithId() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });

        mongoTemplate.save(expectedOrchestrationPlan);

        expectedOrchestrationPlan = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan = mongoTemplate.save(expectedOrchestrationPlan);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", expectedOrchestrationPlan.getId());
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> actualOrchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });

        assertEquals(List.of(expectedOrchestrationPlan).size(), actualOrchestrationPlanList.size());
        Assertions.assertEquals(expectedOrchestrationPlan.getId(), actualOrchestrationPlanList.get(0).getId());
        assertThat(orchestrationPlanMapper.toDto(expectedOrchestrationPlan))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan.Fields.href)
                .withComparatorForType(
                        Comparator.comparing(a -> a.truncatedTo(ChronoUnit.MILLIS)),
                        LocalDateTime.class
                )
                .isEqualTo(actualOrchestrationPlanList.get(0));
        assertHeaders(resultActions, 1, 1);
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchOrchestrationPlanByState_thenReturnedOrchestrationPlanWithState() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));
        mongoTemplate.save(expectedOrchestrationPlan3);

        expectedOrchestrationPlan3 = mongoTemplate.save(expectedOrchestrationPlan3);
        List<OrchestrationPlan> expectedOrchestrationPlans = new ArrayList<>(List.of(expectedOrchestrationPlan1, expectedOrchestrationPlan3));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("state", expectedOrchestrationPlan1.getState().value().toUpperCase());
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> actualOrchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });

        assertEquals(expectedOrchestrationPlans.size(), actualOrchestrationPlanList.size());
        Set<String> expectedOrchestrationPlanIds = new HashSet<>();
        expectedOrchestrationPlans.forEach(expectedOrchestrationPlan ->
                expectedOrchestrationPlanIds.add(expectedOrchestrationPlan.getId())
        );
        actualOrchestrationPlanList.forEach(actualOrchestrationPlan -> {
            assertTrue(expectedOrchestrationPlanIds.contains(actualOrchestrationPlan.getId()));
        });

        assertHeaders(resultActions, 2, 2);
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchOrchestrationPlanByArchived_thenReturnedOrchestrationPlanWithState() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan1.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan1.setArchived(Boolean.TRUE);
        mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setArchived(Boolean.FALSE);
        mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan3.setArchived(Boolean.TRUE);
        mongoTemplate.save(expectedOrchestrationPlan3);

        List<OrchestrationPlan> expectedOrchestrationPlans = new ArrayList<>(List.of(expectedOrchestrationPlan1, expectedOrchestrationPlan3));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("archived", Boolean.TRUE.toString());
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> actualOrchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });

        assertEquals(expectedOrchestrationPlans.size(), actualOrchestrationPlanList.size());
        Set<String> expectedOrchestrationPlanIds = new HashSet<>();
        expectedOrchestrationPlans.forEach(expectedOrchestrationPlan ->
                expectedOrchestrationPlanIds.add(expectedOrchestrationPlan.getId())
        );
        actualOrchestrationPlanList.forEach(actualOrchestrationPlan -> {
            assertTrue(expectedOrchestrationPlanIds.contains(actualOrchestrationPlan.getId()));
        });

        assertHeaders(resultActions, 2, 2);
    }

    @Test
    void givenZeroOrchestrationPlan_whenFetchOrchestrationPlans_thenReturnedEmptyList() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchOrchestrationPlanSortedByReceivedDate_thenReturnedOrchestrationPlansWithReceivedDateAscending() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan1 = mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        expectedOrchestrationPlan2 = mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));

        expectedOrchestrationPlan3 = mongoTemplate.save(expectedOrchestrationPlan3);

        List<OrchestrationPlan> expectedOrchestrationPlans = new ArrayList<>(List.of(expectedOrchestrationPlan1, expectedOrchestrationPlan2, expectedOrchestrationPlan3));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("sort", "receivedDate");
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> actualOrchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });

        assertEquals(expectedOrchestrationPlans.size(), actualOrchestrationPlanList.size());
        assertThat(actualOrchestrationPlanList).extracting("receivedDate").isSorted();
        assertHeaders(resultActions, 3, 3);
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchOrchestrationPlanSortedByPlusReceivedDate_thenReturnedOrchestrationPlansWithReceivedDateAscending() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan1 = mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        expectedOrchestrationPlan2 = mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));

        expectedOrchestrationPlan3 = mongoTemplate.save(expectedOrchestrationPlan3);

        List<OrchestrationPlan> expectedOrchestrationPlans = new ArrayList<>(List.of(expectedOrchestrationPlan1, expectedOrchestrationPlan2, expectedOrchestrationPlan3));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("sort", "+receivedDate");
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> actualOrchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });

        assertEquals(expectedOrchestrationPlans.size(), actualOrchestrationPlanList.size());
        assertThat(actualOrchestrationPlanList).extracting("receivedDate").isSorted();
        assertHeaders(resultActions, 3, 3);
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchOrchestrationPlanSortedByReceivedDateDescending_thenReturnedOrchestrationPlansWithReceivedDateDescending() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan1 = mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        expectedOrchestrationPlan2 = mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));

        expectedOrchestrationPlan3 = mongoTemplate.save(expectedOrchestrationPlan3);

        List<OrchestrationPlan> expectedOrchestrationPlans = new ArrayList<>(List.of(expectedOrchestrationPlan1, expectedOrchestrationPlan2, expectedOrchestrationPlan3));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("sort", "-receivedDate");
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> actualOrchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });

        assertEquals(expectedOrchestrationPlans.size(), actualOrchestrationPlanList.size());
        assertThat(actualOrchestrationPlanList).extracting("receivedDate").isSortedAccordingTo(reverseOrder());
        assertHeaders(resultActions, 3, 3);
    }

    @Test
    void givenRequestWithoutAuthorities_whenFetchOrchestrationPlan_thenReturnedForbidden() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        mongoTemplate.save(expectedOrchestrationPlan1);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt()))
                .andExpect(status().isForbidden());
        resultActions.andReturn().getResponse().getContentAsString();
    }

    @Test
    void givenSavedOrchestrationPlan_whenFetchOrchestrationPlansByState_thenReturnedSaved() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });

        mongoTemplate.save(expectedOrchestrationPlan);


        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL + "?state=" + OrchestrationPlanStateEnum.INITIALIZED.getValue())
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> orchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });

        assertEquals(List.of(expectedOrchestrationPlan).size(), orchestrationPlanList.size());
        assertThat(orchestrationPlanMapper.toDto(expectedOrchestrationPlan))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan.Fields.href)
                .withComparatorForType(
                        Comparator.comparing(a -> a.truncatedTo(ChronoUnit.MILLIS)),
                        LocalDateTime.class
                )
                .isEqualTo(orchestrationPlanList.get(0));
        assertHeaders(resultActions, 1, 1);
        assertThat(resultActions.andReturn().getResponse().getHeader("Link"))
                .contains("state=" + OrchestrationPlanStateEnum.INITIALIZED.getValue());
    }

    @Test
    void givenSavedOrchestrationPlan_whenFetchOrchestrationPlansByNodesState_thenReturnedSaved() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });

        mongoTemplate.save(expectedOrchestrationPlan);


        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL + "?orchestrationPlanNodes.state=" + OrchestrationPlanNodeStateEnum.INITIALIZED.getValue())
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> orchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });

        assertEquals(List.of(expectedOrchestrationPlan).size(), orchestrationPlanList.size());
        assertThat(orchestrationPlanMapper.toDto(expectedOrchestrationPlan))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan.Fields.href)
                .withComparatorForType(
                        Comparator.comparing(a -> a.truncatedTo(ChronoUnit.MILLIS)),
                        LocalDateTime.class
                )
                .isEqualTo(orchestrationPlanList.get(0));
        assertHeaders(resultActions, 1, 1);
        assertThat(resultActions.andReturn().getResponse().getHeader("Link"))
                .contains("orchestrationPlanNodes.state=" + OrchestrationPlanNodeStateEnum.INITIALIZED.getValue());
    }

    @Test
    void givenOrchestrationPlan_whenFetchOrchestrationPlansWithInvalidSortParam_thenBadRequestReturned() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        mongoTemplate.save(expectedOrchestrationPlan);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL + "?sort=invalidParam")
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isBadRequest());

        Error error = readJson(resultActions, new TypeReference<>() {
        });

        assertEquals("Invalid sort value.", error.getMessage());
        assertEquals(String.valueOf(COOD_VALIDATION_INVALID_QUERY_EXCEPTION.getErrorCode()), error.getCode());
    }

    @Test
    void givenOrchestrationPlans_whenFetchOrchestrationPlansSortByArchivedAndReceivedDate_thenReturnedSaved() throws Exception {
        OrchestrationPlan orchestrationPlan1 = OrchestrationPlan.builder()
                .id("1")
                .archived(true)
                .receivedDate(Instant.now())
                .build();
        mongoTemplate.save(orchestrationPlan1);

        OrchestrationPlan orchestrationPlan2 = OrchestrationPlan.builder()
                .id("2")
                .archived(true)
                .receivedDate(Instant.now().minusSeconds(60))
                .build();
        mongoTemplate.save(orchestrationPlan2);

        OrchestrationPlan orchestrationPlan3 = OrchestrationPlan.builder()
                .id("3")
                .archived(false)
                .receivedDate(Instant.now())
                .build();
        mongoTemplate.save(orchestrationPlan3);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL + "?sort=archived,receivedDate")
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());

        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> orchestrationPlanList = readJson(resultActions, new TypeReference<>() {
        });

        assertThat(orchestrationPlanList.get(0).getId()).isEqualTo(orchestrationPlan3.getId());
        assertThat(orchestrationPlanList.get(1).getId()).isEqualTo(orchestrationPlan2.getId());
        assertThat(orchestrationPlanList.get(2).getId()).isEqualTo(orchestrationPlan1.getId());
    }

    @Test
    void givenNoOrchestrationPlan_whenFetchOrchestrationPlansByState_thenReturnedSaved() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL + "?state=" + OrchestrationPlanStateEnum.INITIALIZED.getValue())
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNoOrchestrationPlan_whenFetchOrchestrationPlansByNodesState_thenReturnedSaved() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL + "?orchestrationPlanNodes.state=" + OrchestrationPlanNodeStateEnum.INITIALIZED.getValue())
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @CsvSource({
            "$, Invalid sort value.",
            "ABC, Invalid sort value.",
            "limit=-1, Invalid limit value.",
            "offset=-1, Invalid offset value.",
            "limit=3300, Limit exceed max value."
    })
    void givenMultipleOrchestrationPlan_whenFetchWithInvalidQueryParam_thenReturnedBadRequestError(String param, String expectedMessage) throws Exception {

        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {});
        mongoTemplate.save(expectedOrchestrationPlan1);

        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {});
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {});
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));
        mongoTemplate.save(expectedOrchestrationPlan3);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (param.contains("=")) {
            String[] parts = param.split("=", 2);
            params.add(parts[0], parts[1]);
        } else {
            params.add("sort", param);
        }

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isBadRequest());

        Error error = readJson(resultActions, new TypeReference<>() {});

        assertEquals(expectedMessage, error.getMessage());
        assertEquals(String.valueOf(COOD_VALIDATION_INVALID_QUERY_EXCEPTION.getErrorCode()), error.getCode());
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchAllOrchestrationPlan_thenReturnedResponseWithOkHttpStatus() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));

        mongoTemplate.save(expectedOrchestrationPlan3);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("offset", "0");
        mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchSomeOrchestrationPlan_thenReturnedResponseWithPartialContentHttpStatus() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));

        mongoTemplate.save(expectedOrchestrationPlan3);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("offset", "0");
        params.add("limit", "2");
        mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isPartialContent());
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchSomeOrchestrationPlanWithRequestedDeliveryDateGtE_thenReturnedResponseWithPartialContentHttpStatus() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan1.setRequestedDeliveryDate(Instant.now().plus(1, ChronoUnit.DAYS));

        mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });

        expectedOrchestrationPlan2.setRequestedDeliveryDate(Instant.now().minus(1, ChronoUnit.DAYS));
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("offset", "0");
        params.add("limit", "2");
        params.add("requestedDeliveryDate.gte", Instant.now().toString());
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());

        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> orchestrationPlans = readJson(resultActions, new TypeReference<>() {
        });
        assertThat(orchestrationPlans).hasSize(1);
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchSomeOrchestrationPlanWithRequestedDeliveryDateLte_thenReturnedResponseWithPartialContentHttpStatus() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan1.setRequestedDeliveryDate(Instant.now().minus(2, ChronoUnit.DAYS));

        mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });

        expectedOrchestrationPlan2.setRequestedDeliveryDate(Instant.now().minus(1, ChronoUnit.DAYS));
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("offset", "0");
        params.add("limit", "2");
        params.add("requestedDeliveryDate.lte", Instant.now().toString());
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());

        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> orchestrationPlans = readJson(resultActions, new TypeReference<>() {
        });
        assertThat(orchestrationPlans).hasSize(2);
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchSomeOrchestrationPlanWithRequestedDeliveryDateGteAndLte_thenReturnedResponseWithPartialContentHttpStatus() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan1.setRequestedDeliveryDate(Instant.now().plus(1, ChronoUnit.DAYS));

        mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });

        expectedOrchestrationPlan2.setRequestedDeliveryDate(Instant.now().minus(1, ChronoUnit.DAYS));
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        mongoTemplate.save(expectedOrchestrationPlan2);

        OrchestrationPlan expectedOrchestrationPlan3 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan3.setId(RandomStringUtils.random(24));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("offset", "0");
        params.add("limit", "2");
        params.add("requestedDeliveryDate.gte", Instant.now().toString());
        params.add("requestedDeliveryDate.lte", Instant.now().plus(2, ChronoUnit.DAYS).toString());
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URL)
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority("x200")))))
                .andExpect(status().isOk());

        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> orchestrationPlans = readJson(resultActions, new TypeReference<>() {
        });
        assertThat(orchestrationPlans).hasSize(1);
    }
}
