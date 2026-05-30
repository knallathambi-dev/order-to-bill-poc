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
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationNodeSchedule;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanSchedule;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class, ResetWireMockExtension.class})
class FetchOrchestrationPlanByIdTests extends BaseAbstractionIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OrchestrationPlanMapper orchestrationPlanMapper;

    private static final String ORCHESTRATION_PLAN_JSON = "/integration/OrchestrationPlanAfterBuild.json";
    private static final String ORCHESTRATION_PLAN_HELD_WITH_ERROR_JSON = "/integration/OrchestrationPlanHeldWithError.json";
    private static final String ORCHESTRATION_PLAN_API_URI_WITH_ID = "/orchestrationPlan/%s";
    private static final String ORCHESTRATION_PLAN_API_URI_WITH_ID_AND_HOST = "http://localhost/orchestrationPlan/%s";

    private static final String ROLE = "x200";

    @Test
    void givenSavedOrchestrationPlan_whenFetchOrchestrationPlanById_thenReturnedSaved() throws Exception {
        String randomId = RandomStringUtils.randomAlphanumeric(24);
        OrchestrationPlanNode node = OrchestrationPlanNode.builder()
                .state(OrchestrationPlanNodeState.COMPLETED)
                .relatedProductOrderItem(List.of())
                .build();

        OrchestrationPlan expectedOrchestrationPlan = OrchestrationPlan.builder()
                .id(randomId)
                .orchestrationPlanNodes(new HashSet<>(List.of(node)))
                .state(State.EXECUTED)
                .build();
        mongoTemplate.save(expectedOrchestrationPlan);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URI_WITH_ID.formatted(randomId))
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority(ROLE)))))
                .andExpect(status().isOk());

        resultActions.andReturn().getResponse().getContentAsString();
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan actualOrchestrationPlan = readJson(resultActions, new TypeReference<>() {
        });

        assertThat(orchestrationPlanMapper.toDto(expectedOrchestrationPlan))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan.Fields.href)
                .ignoringFieldsMatchingRegexes(".*atType")
                .withComparatorForType(
                        Comparator.comparing(a -> ((OffsetDateTime)a).truncatedTo(ChronoUnit.MILLIS)),
                        OffsetDateTime.class
                )
                .isEqualTo(actualOrchestrationPlan);
        assertThat(actualOrchestrationPlan.getHref())
                .isNotBlank()
                .isNotEmpty()
                .isEqualTo(ORCHESTRATION_PLAN_API_URI_WITH_ID_AND_HOST.formatted(expectedOrchestrationPlan.getId()));
    }

    @Test
    void givenSavedOrchestrationPlan_whenFetchOrchestrationPlanById_thenReturnedLeadTimeInfo() throws Exception {
        String randomId = RandomStringUtils.randomAlphanumeric(24);
        OrchestrationPlanNode node = OrchestrationPlanNode.builder()
                .state(OrchestrationPlanNodeState.COMPLETED)
                .orchestrationNodeSchedule(OrchestrationNodeSchedule.builder()
                        .actualOrderItemCompletionDate(Instant.now())
                        .build())
                .relatedProductOrderItem(List.of())
                .build();

        OrchestrationPlan expectedOrchestrationPlan = OrchestrationPlan.builder()
                .id(randomId)
                .orchestrationPlanNodes(new HashSet<>(List.of(node)))
                .state(State.EXECUTED)
                .orchestrationPlanSchedule(OrchestrationPlanSchedule.builder()
                        .actualOrderStartDate(Instant.now().minus(1, ChronoUnit.DAYS))
                        .orderStartDate(Instant.now().minus(1, ChronoUnit.DAYS))
                        .estimatedOrderDeliveryLeadTime(86400L)
                        .build())
                .build();
        mongoTemplate.save(expectedOrchestrationPlan);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URI_WITH_ID.formatted(expectedOrchestrationPlan.getId()))
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority(ROLE)))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan actualOrchestrationPlan = readJson(resultActions, new TypeReference<>() {
        });

        assertThat(actualOrchestrationPlan.getOrchestrationPlanSchedule().getActualOrderDeliveryLeadTime()).isNotNull();
        assertThat(actualOrchestrationPlan.getOrchestrationPlanSchedule().getExpectedOrderCompletionDate()).isNotNull();
    }

    @Test
    void givenSavedHeldOrchestrationPlan_whenFetchOrchestrationPlanById_thenReturnedSavedWithErrorMessage() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_HELD_WITH_ERROR_JSON, new TypeReference<>() {
        });

        expectedOrchestrationPlan = mongoTemplate.save(expectedOrchestrationPlan);
        expectedOrchestrationPlan.setId(RandomStringUtils.randomAlphanumeric(24));
        mongoTemplate.save(expectedOrchestrationPlan);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URI_WITH_ID.formatted(expectedOrchestrationPlan.getId()))
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority(ROLE)))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan actualOrchestrationPlan = readJson(resultActions, new TypeReference<>() {
        });

        assertThat(orchestrationPlanMapper.toDto(expectedOrchestrationPlan))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan.Fields.href)
                .withComparatorForType(
                        Comparator.comparing(a -> a.truncatedTo(ChronoUnit.MILLIS)),
                        LocalDateTime.class
                )
                .isEqualTo(actualOrchestrationPlan);
        assertThat(actualOrchestrationPlan.getHref())
                .isNotBlank()
                .isNotEmpty()
                .isEqualTo(ORCHESTRATION_PLAN_API_URI_WITH_ID_AND_HOST.formatted(expectedOrchestrationPlan.getId()));
    }


    @Test
    void givenSavedOrchestrationPlan_whenFetchOrchestrationPlanByIdAndEmptyFields_thenBadRequestReturned() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });

        expectedOrchestrationPlan = mongoTemplate.save(expectedOrchestrationPlan);
        expectedOrchestrationPlan.setId(RandomStringUtils.randomAlphanumeric(24));
        mongoTemplate.save(expectedOrchestrationPlan);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URI_WITH_ID.formatted(expectedOrchestrationPlan.getId()))
                        .queryParam("fields", "")
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority(ROLE)))))
                .andExpect(status().isBadRequest());

        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Error error = readJson(resultActions, new TypeReference<>() {
        });
        assertEquals("28", error.getCode());
        assertEquals("if id is null or fields are empty", error.getReason());
        assertEquals("Query param [ Fields ] should not be empty or null", error.getMessage());
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchOrchestrationPlanById_thenReturnedOrchestrationPlanWithId() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });

        mongoTemplate.save(expectedOrchestrationPlan);

        expectedOrchestrationPlan = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan.setId(RandomStringUtils.randomAlphanumeric(24));
        expectedOrchestrationPlan = mongoTemplate.save(expectedOrchestrationPlan);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URI_WITH_ID.formatted(expectedOrchestrationPlan.getId()))
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority(ROLE)))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan actualOrchestrationPlan = readJson(resultActions, new TypeReference<>() {
        });

        Assertions.assertEquals(expectedOrchestrationPlan.getId(), actualOrchestrationPlan.getId());
        assertThat(orchestrationPlanMapper.toDto(expectedOrchestrationPlan))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan.Fields.href)
                .withComparatorForType(
                        Comparator.comparing(a -> a.truncatedTo(ChronoUnit.MILLIS)),
                        LocalDateTime.class
                )
                .isEqualTo(actualOrchestrationPlan);
        assertThat(actualOrchestrationPlan.getHref())
                .isNotBlank()
                .isNotEmpty()
                .isEqualTo(ORCHESTRATION_PLAN_API_URI_WITH_ID_AND_HOST.formatted(expectedOrchestrationPlan.getId()));
    }

    @Test
    void givenMultipleOrchestrationPlan_whenFetchOrchestrationPlanByIdWithFields_thenReturnedOrchestrationPlan() throws Exception {
        OrchestrationPlan expectedOrchestrationPlan1 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        mongoTemplate.save(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = JsonUtil.readObjectFromResource(ORCHESTRATION_PLAN_JSON, new TypeReference<>() {
        });
        expectedOrchestrationPlan2.setId(RandomStringUtils.random(24));
        expectedOrchestrationPlan2.setState(State.IN_PROGRESS);
        expectedOrchestrationPlan2 = mongoTemplate.save(expectedOrchestrationPlan2);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("fields", "receivedDate,id,relatedProductOrder.id,requestedDeliveryDate,relatedParty,state");
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URI_WITH_ID.formatted(expectedOrchestrationPlan2.getId()))
                        .params(params)
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority(ROLE)))))
                .andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan actualOrchestrationPlan = readJson(resultActions, new TypeReference<>() {
        });
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan expectedOrchestrationPlanDto = orchestrationPlanMapper.toDto(expectedOrchestrationPlan2);
        assertEquals(expectedOrchestrationPlanDto.getId(), actualOrchestrationPlan.getId());
        assertEquals(expectedOrchestrationPlanDto.getReceivedDate().truncatedTo(ChronoUnit.SECONDS), actualOrchestrationPlan.getReceivedDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(expectedOrchestrationPlanDto.getRequestedDeliveryDate().truncatedTo(ChronoUnit.SECONDS), actualOrchestrationPlan.getRequestedDeliveryDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(expectedOrchestrationPlanDto.getRelatedParty(), actualOrchestrationPlan.getRelatedParty());
        assertEquals(expectedOrchestrationPlanDto.getState(), actualOrchestrationPlan.getState());
        assertNull(actualOrchestrationPlan.getOrchestrationPlanNodes());

    }

    @Test
    void givenZeroOrchestrationPlan_whenFetchOrchestrationPlans_thenReturnedEmptyList() throws Exception {
        String randomId = RandomStringUtils.randomAlphanumeric(24);
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URI_WITH_ID.formatted(randomId))
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority(ROLE)))))
                .andExpect(status().isNotFound());

        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Error error = readJson(resultActions, new TypeReference<>() {
        });
        assertEquals("60", error.getCode());
        assertEquals("orchestration plan doesn't exist", error.getReason());
        assertEquals("No orchestrationPlan found with id: %s".formatted(randomId), error.getMessage());

    }

    @Test
    void givenRequestWithoutAuthorities_whenFetchOrchestrationPlans_thenReturnedForbidden() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                        .get(ORCHESTRATION_PLAN_API_URI_WITH_ID.formatted(RandomStringUtils.random(24)))
                        .with(jwt()))
                .andExpect(status().isForbidden());
    }

}
