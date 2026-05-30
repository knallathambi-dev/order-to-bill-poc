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
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.service.SecurityService;
import com.orange.discobole.productinventory.util.AbstractJobsTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.*;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.CANNOT_UPLOAD_FILE;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.THE_JOB_WITH_ID_S_DOES_NOT_EXIST;
import static com.orange.discobole.productinventory.util.creator.JobSpecificationCreator.createPurgeJobSpecificationBuilderWithJobScheduler;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class UploadProductsApiTest extends AbstractJobsTest {

    @MockBean
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        createBucket(s3Client);
    }

    @AfterEach
    public void tearDown() {
        deleteBucketAndAllContents(s3Client);
    }

    @Test
    void givenNonNullFileAndImportJobIdOfFuturePlannedDateJob_whenUploadFile_thenCreated() throws Exception {
        OffsetDateTime futurePlannedDate = OffsetDateTime.now().plusHours(30).withNano(0).withOffsetSameInstant(ZoneOffset.UTC);
        List<JobEntity> jobEntities = createImportJob(futurePlannedDate);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_GET_UPLOAD_FILE_URL, jobEntities.get(0).getId()));

        resultActions.andExpect(status().isOk());
        FileInformation fileInformation = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        Assertions.assertNotNull(fileInformation.getUrl(), "Pre-signed URL should not be null");
        List<Product> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID)
                    .build());
        }
        uploadToS3(list, fileInformation.getUrl(), APPLICATION_JSON.getMimeType());

        JobEntity jobEntityById = getJobEntityById(jobEntities.get(0).getId());
        awaitAndAssertFileCreated(s3Client, jobEntityById.getFileName());
        List<JobEntity> updatedJobEntities = getJobEntities(jobEntityById.getJobSpecification().getId());
        Assertions.assertNotNull(updatedJobEntities.get(0));
        assertEquals(jobEntityById.getFileName(), updatedJobEntities.get(0).getFileName());
    }

    @Test
    void givenNonNullFileAndInvalidJobId_whenUploadFile_thenBadRequest() throws Exception {
        String invalidJobId = "12345";
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_GET_UPLOAD_FILE_URL, invalidJobId));

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertNotFoundErrorExists(error, RESOURCE_NOT_FOUND.getCode(), String.format(THE_JOB_WITH_ID_S_DOES_NOT_EXIST, invalidJobId), RESOURCE_NOT_FOUND.getStatus());
    }

    @Test
    void givenNonNullFileAndPurgeJobId_whenUploadFile_thenBadRequest() throws Exception {
        when(securityService.hasRoleEntitlement(Roles.PURGE_PRODUCT)).thenReturn(true);
        String purgeQuery = "status=Terminated&creationDate.lte=" + dateFormatter.format(OffsetDateTime.now());
        String purgeJob = toJsonString(
                createPurgeJobSpecificationBuilderWithJobScheduler(purgeQuery, PurgeTypeEnum.PURGEPRODUCT,
                        OneTimeJobScheduler
                                .builder()
                                .plannedDate(OffsetDateTime.now().plusSeconds(60))
                                .build()).build());
        ResultActions resultActions = callRestfulEndpoint(
                mockMvc,
                POST,
                PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION,
                contentBodyJson(purgeJob),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        PurgeJobSpecification purgeJobSpecification = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        List<JobEntity> jobEntities = getJobEntities(purgeJobSpecification.getId());
        assertNotNull(jobEntities);
        Assertions.assertEquals(JobTypeEnum.PURGEJOB, jobEntities.get(0).getAtType());
        assertNotNull(jobEntities.get(0).getId());
        ResultActions uploadFileResultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_GET_UPLOAD_FILE_URL, jobEntities.get(0).getId()));

        Error error = readJsonFromAPIResponse(uploadFileResultActions, new TypeReference<>() {
        });
        assertNotImplementedErrorExists(error, NOT_IMPLEMENTED.getCode(), String.format("This operation is not Implemented for this job Type: %s", JobTypeEnum.PURGEJOB), HttpStatus.NOT_IMPLEMENTED.getReasonPhrase());
    }

    @Test
    void givenNonNullFileAndJobIdOfRunningImportJob_whenUploadFile_thenBadRequest() throws Exception {

        OffsetDateTime futurePlannedDate = OffsetDateTime.now().plusHours(30).withNano(0).withOffsetSameInstant(ZoneOffset.UTC);
        List<JobEntity> jobEntities = createImportJob(futurePlannedDate);
        jobEntities.get(0).setStatus(JobStatusType.RUNNING);
        mongoTemplate.save(jobEntities.get(0));
        ResultActions uploadFileResultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_GET_UPLOAD_FILE_URL, jobEntities.get(0).getId()));

        Error error = readJsonFromAPIResponse(uploadFileResultActions, new TypeReference<>() {
        });
        assertConflictErrorExists(error, CONFLICT.getCode(), String.format(CANNOT_UPLOAD_FILE, JobStatusType.RUNNING), CONFLICT.getStatus());
    }


}

