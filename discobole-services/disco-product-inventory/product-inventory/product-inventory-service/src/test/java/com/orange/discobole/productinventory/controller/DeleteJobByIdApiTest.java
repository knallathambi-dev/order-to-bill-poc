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
import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.v1.JobSpecificationStatusType;
import com.orange.discobole.productinventory.dto.v1.JobSpecificationType;
import com.orange.discobole.productinventory.dto.v1.TimePeriod;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_BY_ID_URI;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.CONFLICT;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.RESOURCE_NOT_FOUND;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.CANNOT_DELETE_ALREADY_STARTED_JOB_SPECIFICATION;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.JOB_SPECIFICATION_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeleteJobByIdApiTest extends AbstractTest {

    @Test
    void givenValidJobSpecificationIdWithStatusStarted_whenDelete_thenJobDeleted() throws Exception {
        String expectedJobSpecificationId = "JobSpecificationId";
        JobSpecificationEntity jobSpecificationEntity = JobSpecificationEntity.builder().id(expectedJobSpecificationId)
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION)
                .name("test")
                .activePeriod(
                        TimePeriod
                                .builder()
                                .startDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .endDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .build())
                .lifecycleStatus(JobSpecificationStatusType.TERMINATED)
                .build();
        mongoTemplate.save(jobSpecificationEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, DELETE, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_BY_ID_URI, expectedJobSpecificationId));
        resultActions.andExpect(status().isNoContent());
        JobSpecificationEntity deletedJobSpecificationEntity = mongoTemplate.findById(expectedJobSpecificationId, JobSpecificationEntity.class);
        assertNull(deletedJobSpecificationEntity, "The jobSpecification should be deleted and not found in the database.");
    }

    @Test
    void givenValidJobSpecificationIdWithStatusStarted_whenDelete_thenJobNotDeleted() throws Exception {
        String expectedJobSpecificationId = "JobSpecificationId";
        JobSpecificationEntity jobSpecificationEntity = JobSpecificationEntity.builder().id(expectedJobSpecificationId)
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION)
                .name("test")
                .activePeriod(
                        TimePeriod
                                .builder()
                                .startDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .endDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .build())
                .lifecycleStatus(JobSpecificationStatusType.ACTIVE)
                .build();
        mongoTemplate.save(jobSpecificationEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, DELETE, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_BY_ID_URI, expectedJobSpecificationId));
        resultActions.andExpect(status().isConflict());


        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertConflictErrorExists(error, CONFLICT.getCode(), CANNOT_DELETE_ALREADY_STARTED_JOB_SPECIFICATION, CONFLICT.getStatus());

    }

    @Test
    void givenNonExistentJobSpecificationId_whenDelete_thenNotFound() throws Exception {
        String invalidJobSpecificationId = "NOT_IN_DATABASE";
        ResultActions resultActions = callRestfulEndpoint(mockMvc, DELETE, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_BY_ID_URI, invalidJobSpecificationId));
        resultActions.andExpect(status().isNotFound());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotFoundErrorExists(
                error,
                RESOURCE_NOT_FOUND.getCode(),
                String.format(JOB_SPECIFICATION_NOT_FOUND, invalidJobSpecificationId),
                RESOURCE_NOT_FOUND.getStatus());


    }
}
