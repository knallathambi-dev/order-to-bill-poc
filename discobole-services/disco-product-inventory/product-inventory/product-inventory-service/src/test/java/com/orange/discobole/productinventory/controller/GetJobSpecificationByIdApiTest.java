// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.productinventory.dto.v1.JobSpecification;
import com.orange.discobole.productinventory.dto.v1.JobSpecificationStatusType;
import com.orange.discobole.productinventory.dto.v1.JobSpecificationType;
import com.orange.discobole.productinventory.dto.v1.TimePeriod;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static com.orange.discobole.productinventory.constant.Constant.FIELDS;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_BY_ID_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetJobSpecificationByIdApiTest extends AbstractTest {

    @Test
    void givenValidJobSpecificationId_whenGetJobByIdWithNoFieldsDefined_thenAllJobFieldsRetrieved() throws Exception {
        String expectedProductId = "JobSpecificationId";
        JobSpecificationEntity jobSpecificationEntity = JobSpecificationEntity.builder().id(expectedProductId)
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION)
                .name("test")
                .activePeriod(
                        TimePeriod
                                .builder()
                                .startDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .endDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC)).build()
                )
                .lifecycleStatus(JobSpecificationStatusType.TERMINATED)
                .build();
        jobSpecificationEntity = mongoTemplate.save(jobSpecificationEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_BY_ID_URI, expectedProductId));
        resultActions.andExpect(status().isOk());
        JobSpecification result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        Assertions.assertThat(result.getId()).isEqualTo(jobSpecificationEntity.getId());
        Assertions.assertThat(result.getAtType()).isEqualTo(jobSpecificationEntity.getAtType().toString());
        Assertions.assertThat(result.getLifecycleStatus()).isEqualTo(jobSpecificationEntity.getLifecycleStatus());
    }

    @Test
    void givenValidJobSpecificationId_whenGetJobByIdWithInvalidFieldsDefined_thenAllProductFieldsRetrievedErrorIgnored() throws Exception {
        String expectedProductId = "JobSpecificationId";
        JobSpecificationEntity jobSpecificationEntity = JobSpecificationEntity.builder().id(expectedProductId)
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION)
                .name("test")
                .activePeriod(
                        TimePeriod
                                .builder()
                                .startDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .endDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC)).build()
                )
                .lifecycleStatus(JobSpecificationStatusType.TERMINATED)
                .build();
        mongoTemplate.save(jobSpecificationEntity);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_BY_ID_URI, expectedProductId), param(Map.of(FIELDS, "id,productOffering,name")));
        resultActions.andExpect(status().isOk());
        JobSpecification result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(jobSpecificationEntity.getId(), result.getId());
        assertEquals(jobSpecificationEntity.getName(), result.getName());
        assertEquals(jobSpecificationEntity.getAtType(), JobSpecificationType.fromValue(result.getAtType()));
        assertNull(result.getLifecycleStatus());
        assertNull(result.getActivePeriod());
    }

    @Test
    void givenInValidJobSpecificationId_whenGetJobById_thenReturnNoContentStatus() throws Exception {
        String expectedProductId = "JobSpecificationId";
        JobSpecificationEntity jobSpecificationEntity = JobSpecificationEntity.builder().id(expectedProductId)
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION)
                .name("test")
                .activePeriod(
                        TimePeriod
                                .builder()
                                .startDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .endDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC)).build()
                )
                .lifecycleStatus(JobSpecificationStatusType.TERMINATED)
                .build();
        String invalidProductId = "invalid";
        mongoTemplate.save(jobSpecificationEntity);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_BY_ID_URI, invalidProductId), param(Map.of(FIELDS, "id,productOffering,name")));
        resultActions.andExpect(status().isNotFound());
    }
}
