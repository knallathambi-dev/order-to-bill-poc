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
import com.orange.discobole.productinventory.converter.JobSpecificationTypeToJobTypeEnumConverter;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntityRef;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.constant.QueryFields.GTE_SUFFIX;
import static com.orange.discobole.productinventory.constant.QueryFields.PLANNED_DATE;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_BY_ID_URI;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_S_JOB;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetJobByJobSpecificationApiTest extends AbstractTest {

    private static JobSpecificationEntity.JobSpecificationEntityBuilder getJobSpecificationEntity(String jobSpecificationId, JobSpecificationType type, JobSpecificationStatusType status) {
        return JobSpecificationEntity.builder().id(jobSpecificationId)
                .atType(type)
                .name("test")
                .creationDate(OffsetDateTime.now())
                .lifecycleStatus(status);
    }

    private static JobEntity.JobEntityBuilder getJobEntityBuilder(String jobId, JobSpecificationEntity jobSpecification, JobStatusType status, OffsetDateTime startDate, OffsetDateTime endDate) {
        return JobEntity
                .builder()
                .id(jobId)
                .executionPeriod(
                        TimePeriod
                                .builder()
                                .startDateTime(startDate)
                                .endDateTime(endDate).build()
                )
                .fileName("fileName")
                .atType(JobSpecificationTypeToJobTypeEnumConverter.convert(jobSpecification.getAtType()))
                .jobSpecification(JobSpecificationEntityRef.builder().id(jobSpecification.getId()).build())
                .status(status);
    }

    private JobCreationResult createJob(JobStatusType jobStatusType, OffsetDateTime startDate, OffsetDateTime endDate) {
        JobSpecificationEntity jobSpecification = getJobSpecificationEntity(ObjectId.get().toString(), JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.CREATED).build();
        jobSpecification = mongoTemplate.save(jobSpecification);
        return createJob(jobStatusType, startDate, endDate, jobSpecification);
    }

    private JobCreationResult createJob(JobStatusType jobStatusType, OffsetDateTime startDate, OffsetDateTime endDate, JobSpecificationEntity jobSpecification) {

        return createJob(jobStatusType, startDate, endDate, null, jobSpecification);
    }

    private JobCreationResult createJob(JobStatusType jobStatusType, OffsetDateTime startDate, OffsetDateTime endDate, OffsetDateTime plannedDate, JobSpecificationEntity jobSpecification) {
        String expectedJobId = ObjectId.get().toString();
        JobEntity jobEntity = getJobEntityBuilder(expectedJobId, jobSpecification, jobStatusType, startDate, endDate).build();
        if (plannedDate != null) {
            jobEntity.setPlannedDate(plannedDate);
        }
        return new JobCreationResult(expectedJobId, mongoTemplate.save(jobEntity));
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithLimit_thenJobsRetrieved() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.now();
        OffsetDateTime endDate = startDate.plusHours(1);
        JobSpecificationEntity jobSpecification = getJobSpecificationEntity(ObjectId.get().toString(), JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).build();
        for (int i = 0; i < 3; i++) {
            jobSpecification = mongoTemplate.save(jobSpecification);
            JobEntity jobEntity = getJobEntityBuilder(ObjectId.get().toString(), jobSpecification, JobStatusType.NOTSTARTED, startDate, endDate).build();
            mongoTemplate.save(jobEntity);
        }

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_S_JOB, jobSpecification.getId()), param(Map.of(LIMIT, "2")));
        resultActions.andExpect(status().isPartialContent());
        List<Job> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(2, result.size());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobByIdWithFieldsDefined_thenJobRetrieved() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.now();
        OffsetDateTime endDate = startDate.plusHours(1);
        JobCreationResult job = createJob(JobStatusType.NOTSTARTED, startDate, endDate);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_S_JOB, job.job.getJobSpecification().getId()), param(Map.of(FIELDS, "id,@type")));
        resultActions.andExpect(status().isOk());
        List<Job> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(job.job.getId(), result.get(0).getId());
        assertNull(result.get(0).getStatus());
        assertNull(result.get(0).getExecutionPeriod());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithFilter_thenJobsRetrieved() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.now();
        OffsetDateTime endDate = startDate.plusHours(1);
        JobSpecificationEntity jobSpecification = getJobSpecificationEntity(ObjectId.get().toString(), JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.CREATED).build();
        jobSpecification = mongoTemplate.save(jobSpecification);
        for (int i = 0; i < 3; i++) {
            JobStatusType status = i % 2 == 0 ? JobStatusType.NOTSTARTED : JobStatusType.SUCCEEDED;
            createJob(status, startDate, endDate, jobSpecification);
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_S_JOB, jobSpecification.getId()),
                param(Map.of(STATUS, JobStatusType.SUCCEEDED.getValue())));
        resultActions.andExpect(status().isOk());
        List<Job> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(1, result.size());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithPlannedDateFilter_thenJobsRetrieved() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.now();
        OffsetDateTime endDate = startDate.plusHours(1);
        JobSpecificationEntity jobSpecification = getJobSpecificationEntity(ObjectId.get().toString(), JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.CREATED).build();
        jobSpecification = mongoTemplate.save(jobSpecification);
        OffsetDateTime plannedDate = OffsetDateTime.now();
        createJob(JobStatusType.NOTSTARTED, startDate, endDate, plannedDate, jobSpecification);
        createJob(JobStatusType.NOTSTARTED, startDate, endDate, plannedDate.minusDays(2), jobSpecification);
        createJob(JobStatusType.NOTSTARTED, startDate, endDate, plannedDate.minusDays(3), jobSpecification);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_S_JOB, jobSpecification.getId()), param(Map.of(PLANNED_DATE + GTE_SUFFIX, plannedDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))));
        resultActions.andExpect(status().isOk());
        List<Job> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(1, result.size());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithInvalidOffset_thenBadRequest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.now();
        OffsetDateTime endDate = startDate.plusHours(1);
        JobSpecificationEntity jobSpecification = getJobSpecificationEntity(ObjectId.get().toString(), JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.CREATED).build();
        jobSpecification = mongoTemplate.save(jobSpecification);
        for (int i = 0; i < 3; i++) {
            createJob(JobStatusType.NOTSTARTED, startDate, endDate, jobSpecification);
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_S_JOB, jobSpecification.getId()), param(Map.of(OFFSET, "5")));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithSorting_thenJobsRetrieved() throws Exception {
        List<String> dateList = List.of(
                "2023-11-20T09:43:16",
                "2020-11-20T09:43:16",
                "2021-11-20T09:43:16"
        );
        String firstJobId = null;
        JobSpecificationEntity jobSpecification = getJobSpecificationEntity(ObjectId.get().toString(), JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.CREATED).build();
        jobSpecification = mongoTemplate.save(jobSpecification);
        for (int i = 0; i < 3; i++) {
            OffsetDateTime date = LocalDateTime.parse(dateList.get(i)).atOffset(ZoneOffset.UTC);
            JobCreationResult job = createJob(JobStatusType.NOTSTARTED, date, date, jobSpecification);
            firstJobId = firstJobId == null ? job.id : firstJobId; //get the id of the first jobSpecification
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_S_JOB, jobSpecification.getId()), param(
                Map.of(
                        SORT, SortJobEnum._EXECUTIONPERIOD_ENDDATETIME.getValue())
        ));

        resultActions.andExpect(status().isOk());
        List<Job> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(firstJobId, result.get(0).getId());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithFields_thenJobsRetrievedWithDefaultFields() {
        OffsetDateTime startDate = OffsetDateTime.now();
        OffsetDateTime endDate = startDate.plusHours(1);
        JobSpecificationEntity jobSpecification = getJobSpecificationEntity(ObjectId.get().toString(), JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.CREATED).build();
        jobSpecification = mongoTemplate.save(jobSpecification);
        for (int i = 0; i < 3; i++) {
            createJob(JobStatusType.NOTSTARTED, startDate, endDate, jobSpecification);
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_S_JOB, jobSpecification), param(Map.of(FIELDS, "id")));
        List<Job> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        for (Job jobSpecification2 : result) {
            assertNotNull(jobSpecification2.getId());
        }
    }

    @Test
    void givenValidJobSpecificationId_whenGetJobByIdWithNoFieldsDefined_thenAllJobFieldsRetrieved() throws Exception {
        JobCreationResult jobResult = createJob(JobStatusType.NOTSTARTED, OffsetDateTime.now(), OffsetDateTime.now().plusHours(1));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_BY_ID_URI, jobResult.id));
        resultActions.andExpect(status().isOk());
        Job result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        Assertions.assertThat(result.getId()).isEqualTo(jobResult.job.getId());
        Assertions.assertThat(result.getStatus()).isEqualTo(jobResult.job.getStatus());
    }

    @Test
    void givenValidJobSpecificationId_whenGetJobByIdWithInvalidFieldsDefined_thenAllProductFieldsRetrievedErrorIgnored() throws Exception {
        JobCreationResult jobResult = createJob(JobStatusType.NOTSTARTED, OffsetDateTime.now(), OffsetDateTime.now().plusHours(1));

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_BY_ID_URI, jobResult.id), param(Map.of(FIELDS, "id,productOffering,name")));
        resultActions.andExpect(status().isOk());
        Job result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(jobResult.job.getId(), result.getId());
        assertNull(result.getStatus());
        assertNull(result.getExecutionPeriod());
    }


    @Test
    void givenInValidJobSpecificationId_whenGetJobById_thenReturnNoContentStatus() throws Exception {
        createJob(JobStatusType.NOTSTARTED, OffsetDateTime.now(), OffsetDateTime.now().plusHours(1));
        String invalidProductId = ObjectId.get().toString();
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_BY_ID_URI, invalidProductId), param(Map.of(FIELDS, "id,productOffering,name")));
        resultActions.andExpect(status().isNotFound());
    }

    private record JobCreationResult(String id, JobEntity job) { //NOSONAR
    }
}
