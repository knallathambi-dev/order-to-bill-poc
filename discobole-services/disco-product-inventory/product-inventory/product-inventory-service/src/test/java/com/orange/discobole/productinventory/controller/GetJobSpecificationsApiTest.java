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
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetJobSpecificationsApiTest extends AbstractTest {

    private static JobSpecificationEntity.JobSpecificationEntityBuilder getJobSpecificationEntityBuilder(String jobSpecificationId, JobSpecificationType type, JobSpecificationStatusType status) {
        return JobSpecificationEntity.builder().id(jobSpecificationId)
                .atType(type)
                .name("test")
                .creationDate(OffsetDateTime.now())
                .lifecycleStatus(status);
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithLimit_thenJobsRetrieved() throws Exception {
        String id1 = "JobSpecificationId1";
        String id2 = "JobSpecificationId2";
        String id3 = "JobSpecificationId3";
        mongoTemplate.save(getJobSpecificationEntityBuilder(id1, JobSpecificationType.TERMINATIONJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).build());
        mongoTemplate.save(getJobSpecificationEntityBuilder(id2, JobSpecificationType.TERMINATIONJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).build());
        mongoTemplate.save(getJobSpecificationEntityBuilder(id3, JobSpecificationType.TERMINATIONJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).build());

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, param(Map.of(LIMIT, "2")));
        resultActions.andExpect(status().isPartialContent());
        List<JobSpecification> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(2, result.size());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithActivePeriodFilter_thenJobsRetrieved() throws Exception {
        OffsetDateTime startDate1 = OffsetDateTime.parse("2023-11-20T09:43:16Z");
        OffsetDateTime endDate1 = startDate1.plusDays(1);

        OffsetDateTime startDate2 = OffsetDateTime.parse("2022-11-20T09:43:16Z");
        OffsetDateTime endDate2 = startDate2.plusDays(1);

        mongoTemplate.save(getJobSpecificationEntityBuilder("JobSpecificationId1", JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED)
                .activePeriod(TimePeriod.builder().startDateTime(startDate1).endDateTime(endDate1).build()).build());
        mongoTemplate.save(getJobSpecificationEntityBuilder("JobSpecificationId2", JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED)
                .activePeriod(TimePeriod.builder().startDateTime(startDate2).endDateTime(endDate2).build()).build());


        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, param(
                Map.of(
                        "activePeriod.startDateTime", startDate1.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        "activePeriod.endDateTime", endDate1.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        AT_TYPE, JobSpecificationType.EXPORTJOBSPECIFICATION.getValue()
                )));

        resultActions.andExpect(status().isOk());

        List<JobSpecification> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});

        assertEquals(1, result.size());
        assertEquals("JobSpecificationId1", result.get(0).getId());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobByIdWithFieldsDefined_thenJobRetrieved() throws Exception {
        String expectedProductId = "JobSpecificationId";
        JobSpecificationEntity jobSpecificationEntity = JobSpecificationEntity.builder().id(expectedProductId)
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION)
                .name("test")
                .activePeriod(TimePeriod
                        .builder()
                        .startDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                        .endDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                        .build())
                .lifecycleStatus(JobSpecificationStatusType.TERMINATED)
                .build();
        mongoTemplate.save(jobSpecificationEntity);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, param(
                Map.of(
                        FIELDS, "id,@type,name",
                        AT_TYPE, JobSpecificationType.TERMINATIONJOBSPECIFICATION.getValue()
                )));
        resultActions.andExpect(status().isOk());
        List<JobSpecification> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotNull(result.get(0).getId());
        assertNotNull(result.get(0).getName());
        assertNotNull(JobSpecificationType.fromValue(result.get(0).getAtType()));
        assertNull(result.get(0).getLifecycleStatus());
        assertNull(result.get(0).getActivePeriod());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithFilter_thenJobsRetrieved() throws Exception {
        String id1 = "JobSpecificationId1";
        String id2 = "JobSpecificationId2";
        String id3 = "JobSpecificationId3";
        mongoTemplate.save(getJobSpecificationEntityBuilder(id1, JobSpecificationType.TERMINATIONJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).creationDate(OffsetDateTime.of(LocalDate.of(2024, 11, 22), LocalTime.of(10, 17, 59), ZoneOffset.UTC)).build());
        mongoTemplate.save(getJobSpecificationEntityBuilder(id2, JobSpecificationType.TERMINATIONJOBSPECIFICATION, JobSpecificationStatusType.CREATED).build());
        mongoTemplate.save(getJobSpecificationEntityBuilder(id3, JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).build());

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION,
                param(Map.of(LIFE_CYCLE_STATUS, JobSpecificationStatusType.TERMINATED.getValue(),
                        NAME, "test",
                        AT_TYPE, JobSpecificationType.TERMINATIONJOBSPECIFICATION.getValue(),
                        CREATION_DATE, "2024-11-22T10:17:59Z")));
        resultActions.andExpect(status().isOk());
        List<JobSpecification> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getCreationDate());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithInvalidOffset_thenBadRequest() throws Exception {
        String id1 = "JobSpecificationId1";
        String id2 = "JobSpecificationId2";
        String id3 = "JobSpecificationId3";
        mongoTemplate.save(getJobSpecificationEntityBuilder(id1, JobSpecificationType.TERMINATIONJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).build());
        mongoTemplate.save(getJobSpecificationEntityBuilder(id2, JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).build());
        mongoTemplate.save(getJobSpecificationEntityBuilder(id3, JobSpecificationType.TERMINATIONJOBSPECIFICATION, JobSpecificationStatusType.CREATED).build());

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, param(Map.of(OFFSET, "5")));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithSorting_thenBadRequest() throws Exception {
        String id1 = "JobSpecificationId1";
        String id2 = "JobSpecificationId2";
        String id3 = "JobSpecificationId3";
        mongoTemplate.save(
                getJobSpecificationEntityBuilder(id1, JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED)
                        .activePeriod(
                                TimePeriod
                                        .builder()
                                        .startDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                        .endDateTime(LocalDateTime.parse("2023-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                        .build())
                        .build());
        mongoTemplate.save(getJobSpecificationEntityBuilder(id2, JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED)
                .activePeriod(
                        TimePeriod
                                .builder()
                                .startDateTime(LocalDateTime.parse("2022-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .endDateTime(LocalDateTime.parse("2022-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .build())
                .build());
        mongoTemplate.save(getJobSpecificationEntityBuilder(id3, JobSpecificationType.EXPORTJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED)
                .activePeriod(
                        TimePeriod
                                .builder()
                                .startDateTime(LocalDateTime.parse("2021-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .endDateTime(LocalDateTime.parse("2021-11-20T09:43:16").atOffset(ZoneOffset.UTC))
                                .build())
                .build());

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, param(
                Map.of(
                        SORT, SortJobSpecificationEnum._ACTIVEPERIOD_ENDDATETIME.getValue(),
                        AT_TYPE, JobSpecificationType.EXPORTJOBSPECIFICATION.getValue()
                )));
        resultActions.andExpect(status().isOk());
        List<JobSpecification> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertEquals(id1, result.get(0).getId());
    }

    @Test
    void givenNonEmptyDatabase_whenGetJobWithFields_thenJobsRetrievedWithDefaultFields() throws Exception {
        // IPCEISCPIB-452: CreationDate always returned in response
        String id1 = "JobSpecificationId1";
        String id2 = "JobSpecificationId2";
        String id3 = "JobSpecificationId3";
        mongoTemplate.save(getJobSpecificationEntityBuilder(id1, JobSpecificationType.TERMINATIONJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).build());
        mongoTemplate.save(getJobSpecificationEntityBuilder(id2, JobSpecificationType.TERMINATIONJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).build());
        mongoTemplate.save(getJobSpecificationEntityBuilder(id3, JobSpecificationType.TERMINATIONJOBSPECIFICATION, JobSpecificationStatusType.TERMINATED).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, param(Map.of(FIELDS, "id")));
        List<JobSpecification> result = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        for (JobSpecification jobSpecification : result) {
            assertNotNull(jobSpecification.getAtType());
            assertNotNull(jobSpecification.getCreationDate());
            assertNotNull(jobSpecification.getId());
        }
    }
}
