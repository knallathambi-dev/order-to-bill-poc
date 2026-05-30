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
import com.orange.discobole.productinventory.constant.Roles;
import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.JobReportProductRefEntity;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobReportEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntityRef;
import com.orange.discobole.productinventory.service.SecurityService;
import com.orange.discobole.productinventory.util.AbstractJobsTest;
import com.orange.discobole.productinventory.validation.query.purge.ProductPurgeTypeQueryValidator;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.orange.discobole.productinventory.constant.Constant.LIFE_CYCLE_STATUS;
import static com.orange.discobole.productinventory.constant.Constant.UNAUTHORIZED_PURGE_MESSAGE;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION;
import static com.orange.discobole.productinventory.dto.v1.PurgeTypeEnum.PURGEJOB;
import static com.orange.discobole.productinventory.dto.v1.PurgeTypeEnum.PURGEPRODUCT;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.*;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static com.orange.discobole.productinventory.util.creator.JobSpecificationCreator.createPurgeJobSpecificationBuilderWithJobScheduler;
import static com.orange.discobole.productinventory.validation.query.purge.JobPurgeTypeQueryValidator.JOB_SPECIFICATION_STATUS_CAN_BE_DELETED;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Slf4j
class PurgeJobTest extends AbstractJobsTest {
    private final OffsetDateTime firstCreationDate = LocalDateTime.of(2024, Month.JANUARY, 30, 5, 5).atOffset(ZoneOffset.UTC);
    private final OffsetDateTime firstStartDate = LocalDateTime.of(2024, Month.JUNE, 5, 5, 5).atOffset(ZoneOffset.UTC);
    private final OffsetDateTime firstTerminationDate = LocalDateTime.of(2024, Month.DECEMBER, 5, 5, 5).atOffset(ZoneOffset.UTC);
    private ProductEntity firstProduct;
    private ProductEntity secondProduct;
    private ProductEntity thirdProduct;
    private PurgeJobSpecification invalidPurgeJobRequest;
    private PurgeJobSpecification invalidPurgeJobJobRequest;
    private PurgeJobSpecification invalidPurgeProductJobRequest;
    private PurgeJobSpecification nullPurgeTypeOnPurgeJobRequest;
    private PurgeJobSpecification invalidPurgeJobQueryStatusJobRequest;
    @MockBean
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        createBucket(s3Client);
        firstProduct = ProductEntity.builder()
                .id(randomAlphabetic(10))
                .creationDate(firstCreationDate)
                .terminationDate(firstTerminationDate)
                .startDate(firstStartDate)
                .status(ProductStatusType.TERMINATED)
                .build();
        secondProduct = ProductEntity.builder()
                .id(randomAlphabetic(10))
                .creationDate(firstCreationDate)
                .terminationDate(firstTerminationDate)
                .startDate(firstStartDate)
                .status(ProductStatusType.ABORTED)
                .build();
        thirdProduct = ProductEntity.builder()
                .id(randomAlphabetic(10))
                .creationDate(OffsetDateTime.now().plusDays(1))
                .terminationDate(firstTerminationDate)
                .startDate(firstStartDate)
                .status(ProductStatusType.ABORTED)
                .build();
        String emptyPurgeQuery = "";
        invalidPurgeJobRequest = PurgeJobSpecification.builder()
                .atType("PurgeJobSpecification")
                .purgeType(PURGEPRODUCT)
                .query(emptyPurgeQuery)
                .schedule(ImmediateJobScheduler.builder().atType("ImmediateJobScheduler").build())
                .build();
        invalidPurgeJobJobRequest = PurgeJobSpecification.builder()
                .atType("PurgeJobSpecification")
                .purgeType(PurgeTypeEnum.PURGEJOB)
                .query(emptyPurgeQuery)
                .schedule(ImmediateJobScheduler.builder().atType("ImmediateJobScheduler").build())
                .build();
        String invalidStatusPurgeJobQuery = "creationDate.lte=" + dateFormatter.format(firstCreationDate)
                + "&creationDate.gte=" + dateFormatter.format(firstCreationDate)
                + "&lifecycleStatus=" + JobSpecificationStatusType.ACTIVE.getValue();
        invalidPurgeJobQueryStatusJobRequest = PurgeJobSpecification.builder()
                .atType("PurgeJobSpecification")
                .purgeType(PurgeTypeEnum.PURGEJOB)
                .query(invalidStatusPurgeJobQuery)
                .schedule(ImmediateJobScheduler.builder().atType("ImmediateJobScheduler").build())
                .build();
        nullPurgeTypeOnPurgeJobRequest = PurgeJobSpecification.builder()
                .atType("PurgeJobSpecification")
                .query("creationDate.lte=" + dateFormatter.format(OffsetDateTime.now()))
                .schedule(ImmediateJobScheduler.builder().atType("ImmediateJobScheduler").build())
                .build();
        String invalidStatusInPurgeProductQuery = String.format(
                "creationDate.gte=%s&creationDate.lte=%s&terminationDate.gte=%s&terminationDate.lte=%s&startDate.gte=%s&startDate.lte=%s&status=%s&status=%s",
                dateFormatter.format(firstCreationDate),
                dateFormatter.format(firstCreationDate),
                dateFormatter.format(firstTerminationDate),
                dateFormatter.format(firstTerminationDate),
                dateFormatter.format(firstStartDate),
                dateFormatter.format(firstStartDate),
                ProductStatusType.ACTIVE.getValue(),
                ProductStatusType.ABORTED.getValue()
        );

        invalidPurgeProductJobRequest = PurgeJobSpecification.builder()
                .atType("PurgeJobSpecification")
                .purgeType(PURGEPRODUCT)
                .query(invalidStatusInPurgeProductQuery)
                .schedule(ImmediateJobScheduler.builder().atType("ImmediateJobScheduler").build())
                .build();

    }

    private PurgeJobSpecification createInvalidPurgeDueToEmptyStatusListRequest(PurgeTypeEnum purgeTypeEnum) {
        return PurgeJobSpecification.builder()
                .atType("PurgeJobSpecification")
                .purgeType(purgeTypeEnum)
                .schedule(ImmediateJobScheduler.builder().atType("ImmediateJobScheduler").build())
                .query(PURGEPRODUCT.equals(purgeTypeEnum) ? "status=" : "lifecycleStatus=")
                .build();
    }

    @Test
    void givenInvalidPurgeJobRequest_whenPurgeProduct_thenBadRequest() {
        when(securityService.hasRoleEntitlement(Roles.PURGE_PRODUCT)).thenReturn(true);
        String validJob = toJsonString(invalidPurgeJobRequest);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), PURGE_QUERY_CANNOT_BE_EMPTY, INVALID_INPUT.getStatus());
    }

    @Test
    void givenInvalidPurgeJobQueryStatusRequest_whenPurgeProduct_thenBadRequest() {
        when(securityService.hasRoleEntitlement(Roles.PURGE_PRODUCT)).thenReturn(true);
        String validJob = toJsonString(invalidPurgeProductJobRequest);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(ALL_STATUS_VALUES_SHOULD_BE_IN, ProductPurgeTypeQueryValidator.PRODUCT_STATUS_CAN_BE_DELETED.stream()
                .map(Object::toString).toList()), INVALID_INPUT.getStatus());
    }

    @Test
    void givenNullPurgeTypeOnPurgeJobTypeRequest_whenPurgeProduct_thenBadRequest() {
        String validJob = toJsonString(nullPurgeTypeOnPurgeJobRequest);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, MISSING_INPUT.getCode(), "purgeType must not be null", MISSING_INPUT.getStatus());
    }

    @Test
    void givenInvalidPurgeJobQueryStatusRequest_whenPurgeJob_thenBadRequest() {
        when(securityService.hasRoleEntitlement(Roles.PURGE_JOB)).thenReturn(true);
        String validJob = toJsonString(invalidPurgeJobQueryStatusJobRequest);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(
                error,
                INVALID_INPUT.getCode(),
                String.format(ALL_LIFE_CYCLE_STATUS_VALUES_SHOULD_BE_IN,
                        JOB_SPECIFICATION_STATUS_CAN_BE_DELETED.stream()
                                .map(Object::toString).toList()), INVALID_INPUT.getStatus());
    }

    @Test
    void givenInvalidPurgeJobRequest_whenPurgeJob_thenBadRequest() {
        when(securityService.hasRoleEntitlement(Roles.PURGE_JOB)).thenReturn(true);
        String validJob = toJsonString(invalidPurgeJobJobRequest);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), PURGE_QUERY_CANNOT_BE_EMPTY, INVALID_INPUT.getStatus());
    }


    @Test
    void givenNonEmptyDatabase_whenCreatePurgeJobWithCreationDateGteQuery_thenJobsAndRelatedFilesPurged() throws Exception {
        when(securityService.hasRoleEntitlement(Roles.PURGE_JOB)).thenReturn(true);
        // Save the initial product entity and wait for 2 seconds
        mongoTemplate.save(firstProduct);
        OffsetDateTime twoSecondsLater = firstCreationDate.plusSeconds(2);
        secondProduct.setCreationDate(twoSecondsLater);
        ProductEntity expected = mongoTemplate.save(secondProduct);
        List<ProductEntity> expectedList = new ArrayList<>();
        expectedList.add(expected);

        // Create the export query with the lastUpdateDateGte value

        String query = "creationDate.gte=" + dateFormatter.format(twoSecondsLater);
        // Create the export jobSpecification
        ResultActions resultActions = createExportJob(
                query,
                ContentTypeEnum.JSON,
                ImmediateJobScheduler
                        .builder()
                        .build(),
                List.of("id", "status", "description"));
        resultActions.andExpect(status().isCreated());

        // Parse the export job from the response
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        // Convert the expected list to an array
        ProductEntity[] array = expectedList.toArray(new ProductEntity[0]);

        // Execute the export jobSpecification
        jobSchedulerService.executeScheduledJobs();
        JobEntity job = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        // Await and assert that the expected entities are in the S3 bucket
        awaitAndAssertExpected(s3Client, job, array);

        OffsetDateTime creationDatePurgeQuery = OffsetDateTime.now();
        // Create the purge query with the creationDateGte value

        String purgeQuery = "lifecycleStatus=Terminated&creationDate.lte=" + dateFormatter.format(creationDatePurgeQuery);

        // Create the purge jobSpecification
        ResultActions purgeResultActions = createPurgeJob(
                purgeQuery,
                PurgeTypeEnum.PURGEJOB,
                ImmediateJobScheduler
                        .builder()
                        .build());
        purgeResultActions.andExpect(status().isCreated());

        // Parse the purge job from the response
        PurgeJobSpecification purgeJob = objectMapper.readValue(purgeResultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        // Find the jobSpecification entity by ID
        JobSpecificationEntity purgejob = mongoTemplate.findById(purgeJob.getId(), JobSpecificationEntity.class);
        assertNotNull(purgejob);
        purgejob.setCreationDate(OffsetDateTime.now().plusMinutes(2));
        mongoTemplate.save(purgejob);
        // Execute the purge jobSpecification
        jobSchedulerService.executeScheduledJobs();
        // Await and assert that the expected entities are deleted from the S3 bucket
        awaitAndAssertDeleted(s3Client, job.getFileName());
        JobEntity deletedJobExecutionExport = mongoTemplate.findById(job.getId(), JobEntity.class);
        assertNull(deletedJobExecutionExport);
        JobSpecificationEntity deletedJobExport = mongoTemplate.findById(job.getId(), JobSpecificationEntity.class);
        assertNull(deletedJobExport);
        verify(securityService).hasRoleEntitlement(Roles.PURGE_JOB);
        JobSpecificationEntity parentJob = mongoTemplate.findById(purgeJob.getId(), JobSpecificationEntity.class);
        Assertions.assertNotNull(parentJob);
        Assertions.assertNotNull(parentJob.getActivePeriod());
    }

    protected ResultActions createPurgeJob(String query, PurgeTypeEnum purgeType,
                                           JobScheduler jobSchedule) {
        String purgeJob = toJsonString(
                createPurgeJobSpecificationBuilderWithJobScheduler(query, purgeType, jobSchedule).build());
        return callRestfulEndpoint(
                mockMvc,
                POST,
                PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION,
                contentBodyJson(purgeJob),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

    }

    protected void awaitAndAssertDeleted(S3Client s3Client, String fileName) {
        await().atMost(120, SECONDS).untilAsserted(() -> {
            try {
                s3Client.getObject(GetObjectRequest.builder().key(fileName).bucket("cpib").build());
            } catch (NoSuchKeyException e) {
                log.error("The specified key does not exist: " + fileName);
                assert true;
            }
        });
    }

    @Test
    void givenNonEmptyDatabase_whenCreatePurgeProductWithCreationDateGteQuery_thenProductsPurged() throws Exception {
        when(securityService.hasRoleEntitlement(Roles.PURGE_PRODUCT)).thenReturn(true);
        // Save the initial product entity and wait for 2 seconds
        mongoTemplate.save(firstProduct);
        mongoTemplate.save(secondProduct);
        mongoTemplate.save(thirdProduct);
        OffsetDateTime fourSecondsLater = OffsetDateTime.now().minusSeconds(1);
        // Create the purge query with the creationDateGte value

        String purgeQuery = "status=Terminated&creationDate.lte=" + dateFormatter.format(fourSecondsLater);

        // Create the purge jobSpecification
        ResultActions purgeResultActions = createPurgeJob(
                purgeQuery,
                PURGEPRODUCT,
                ImmediateJobScheduler
                        .builder()
                        .build());
        purgeResultActions.andExpect(status().isCreated());

        // Parse the purge job from the response
        PurgeJobSpecification purgeJob = objectMapper.readValue(purgeResultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        // Find the jobSpecification entity by ID
        JobSpecificationEntity purgejob = mongoTemplate.findById(purgeJob.getId(), JobSpecificationEntity.class);
        assertNotNull(purgejob);

        // Execute the purge jobSpecification
        jobSchedulerService.executeScheduledJobs();
        List<ProductEntity> resultedProductEntities = mongoTemplate.findAll(ProductEntity.class);
        assertThat(resultedProductEntities).hasSize(2);
        verify(securityService).hasRoleEntitlement(Roles.PURGE_PRODUCT);
    }

    @Test
    void givenInvalidPurgeJobQueryRequest_whenPurgeProduct_thenBadRequest() {
        when(securityService.hasRoleEntitlement(Roles.PURGE_PRODUCT)).thenReturn(true);
        String validJob = toJsonString(createInvalidPurgeDueToEmptyStatusListRequest(PURGEPRODUCT));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), INVALID_FILTER_FORMAT, INVALID_INPUT.getStatus());
        verify(securityService).hasRoleEntitlement(Roles.PURGE_PRODUCT);
    }

    @Test
    void givenInvalidPurgeJobQueryRequest_whenPurgeJob_thenBadRequest() {
        when(securityService.hasRoleEntitlement(Roles.PURGE_JOB)).thenReturn(true);
        String validJob = toJsonString(createInvalidPurgeDueToEmptyStatusListRequest(PURGEJOB));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(
                THE_QUERY_SHOULD_INCLUDE_AT_LEAST_ON_OF_THESE_STATUS_VALUES, LIFE_CYCLE_STATUS, JOB_SPECIFICATION_STATUS_CAN_BE_DELETED), INVALID_INPUT.getStatus());
        verify(securityService).hasRoleEntitlement(Roles.PURGE_JOB);
    }

    @Test
    void givenNonEmptyDatabase_whenCreatePurgeJob_thenUnauthorized() {
        when(securityService.hasRoleEntitlement(Roles.PURGE_PRODUCT)).thenReturn(false);
        // Save the initial product entity and wait for 2 seconds
        mongoTemplate.save(firstProduct);
        mongoTemplate.save(secondProduct);
        mongoTemplate.save(thirdProduct);
        OffsetDateTime fourSecondsLater = OffsetDateTime.now().minusSeconds(1);
        // Create the purge query with the creationDateGte value

        String purgeQuery = "creationDate.lte=" + dateFormatter.format(fourSecondsLater);

        String inValidPurgeJob = toJsonString(
                PurgeJobSpecification
                        .builder()
                        .atType("PurgeJobSpecification")
                        .purgeType(PURGEPRODUCT)
                        .query(purgeQuery)
                        .build()
                        .schedule(ImmediateJobScheduler
                                .builder()
                                .build()));
        // Create the purge jobSpecification
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(inValidPurgeJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertAccessDeniedErrorExists(error, ACCESS_DENIED.getCode(), UNAUTHORIZED_PURGE_MESSAGE, ACCESS_DENIED.getStatus());
        verify(securityService).hasRoleEntitlement(Roles.PURGE_PRODUCT);
    }

    @Test
    void givenBatchJobsWithJobItemNotEmpty_whenCreatePurgeJob_thenBatchJobsAndJobItemsDeleted() throws Exception {
        //given
        when(securityService.hasRoleEntitlement(Roles.PURGE_JOB)).thenReturn(true);
        OffsetDateTime firstBatchJobCreationDate = LocalDateTime.of(2024, Month.SEPTEMBER, 27, 5, 5).atOffset(ZoneOffset.UTC);

        JobSpecificationEntity jobBatch1 = JobSpecificationEntity.builder()
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION)
                .creationDate(firstBatchJobCreationDate)
                .build();
        JobSpecificationEntity batch1 = mongoTemplate.save(jobBatch1);
        assertNotNull(batch1.getId());

        JobEntity job = mongoTemplate.save(JobEntity.builder()
                .status(JobStatusType.NOTSTARTED)
                .jobSpecification(JobSpecificationEntityRef.builder().id(batch1.getId()).build())
                .atType(JobTypeEnum.TERMINATIONJOB)
                .plannedDate(OffsetDateTime.now()).build());
        assertNotNull(job.getId());

        JobReportEntity terminationJobReportEntity = mongoTemplate.save(JobReportEntity
                .builder()
                .jobSpecificationId(batch1.getId())
                .jobId(job.getId())
                .failedProducts(List.of(JobReportProductRefEntity.builder().name("name").id(ObjectId.get().toString()).atType("Product").build()))
                .succeededProducts(List.of(JobReportProductRefEntity.builder().name("name").id(ObjectId.get().toString()).atType("Product").build()))
                .build());
        assertNotNull(terminationJobReportEntity.getId());
        //when create the purge jobSpecification

        String purgeQuery = "lifecycleStatus=Terminated&creationDate.lte=" + dateFormatter.format(firstBatchJobCreationDate);

        ResultActions purgeResultActions = createPurgeJob(
                purgeQuery,
                PurgeTypeEnum.PURGEJOB,
                ImmediateJobScheduler
                        .builder()
                        .build());
        purgeResultActions.andExpect(status().isCreated());
        jobSchedulerService.executeScheduledJobs();

        //then
        JobSpecificationEntity deletedJobBatch = mongoTemplate.findById(batch1.getId(), JobSpecificationEntity.class);
        assertNull(deletedJobBatch);
        JobEntity jobEntity = mongoTemplate.findById(job.getId(), JobEntity.class);
        assertNull(jobEntity);
        JobReportEntity terminationJobReportEntity1 = mongoTemplate.findById(terminationJobReportEntity.getId(), JobReportEntity.class);
        assertNull(terminationJobReportEntity1);
        verify(securityService).hasRoleEntitlement(Roles.PURGE_JOB);
    }
}
