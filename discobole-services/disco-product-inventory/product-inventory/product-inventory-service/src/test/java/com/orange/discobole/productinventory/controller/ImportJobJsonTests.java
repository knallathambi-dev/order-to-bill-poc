// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.util.AbstractJobsTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_GET_UPLOAD_FILE_URL;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_BY_ID_URI;
import static com.orange.discobole.productinventory.dto.v1.ProductRelationshipType.*;
import static com.orange.discobole.productinventory.service.impl.ProductImportServiceImpl.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ImportJobJsonTests extends AbstractJobsTest {
    public static final String CONTRACT_1_JSON_PATH = "src/test/resources/json/importJob/c1.json";
    public static final String CONTRACT_2_JSON_PATH = "src/test/resources/json/importJob/c2.json";
    public static final String CONTRACT_3_JSON_PATH = "src/test/resources/json/importJob/c3.json";
    public static final String CONTRACT_1_CONTRACT_2_JSON_PATH = "src/test/resources/json/importJob/c1c2.json";
    public static final String CONTRACT_1_CONTRACT_2_MISSING_PRODUCT_JSON_PATH = "src/test/resources/json/importJob/c1c2-missing-prd.json";
    public static final String CONTRACT_1_CONTRACT_2_NO_ROOT_JSON_PATH = "src/test/resources/json/importJob/C3-no-root.json";
    public static final String CONTRACT_1_CONTRACT_2_INVALID_JSON_PATH = "src/test/resources/json/importJob/c3-invalid.json";
    public static final String CONTRACT_1_CONTRACT_2_NO_CLEAR_ROOT_JSON_PATH = "src/test/resources/json/importJob/c3-no-clear-root.json";
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        createBucket(s3Client);
    }

    @AfterEach
    void tearDown() {
        deleteBucketAndAllContents(s3Client);
    }

    private static void assertJobFailed(ImportJob importJob, int failedProductsCount, int succeededProductsCount) {
        Assertions.assertNotNull(importJob);
        Assertions.assertNotNull(importJob.getFailureReport());
        Assertions.assertNotNull(importJob.getSuccessReport());
        assertEquals(failedProductsCount, importJob.getFailureReport().getProducts().size());
        assertEquals(succeededProductsCount, importJob.getSuccessReport().getProducts().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {CONTRACT_1_JSON_PATH, CONTRACT_2_JSON_PATH, CONTRACT_3_JSON_PATH, CONTRACT_1_CONTRACT_2_JSON_PATH})
    void givenImportJob_withValidFiles_whenRunningScheduler_thenImported(String filePath) throws Exception {
        List<JobEntity> jobEntities = createImportJob(null);
        String body = Files.readString(Paths.get(filePath));
        uploadFileAndAssert(jobEntities.get(0).getId(), body);
        jobSchedulerService.executeScheduledJobs();
        ImportJob importJob = getImportById(jobEntities.get(0).getId());
        assertJobSucceeded(importJob, body);
    }

    @Test
    void givenImportJob_withHierarchyMissingProduct_whenRunningScheduler_thenHaveFailedProducts() throws Exception {
        List<JobEntity> jobEntities = createImportJob(null);
        String body = Files.readString(Paths.get(CONTRACT_1_CONTRACT_2_MISSING_PRODUCT_JSON_PATH));
        uploadFileAndAssert(jobEntities.get(0).getId(), body);
        jobSchedulerService.executeScheduledJobs();
        ImportJob importJob = getImportById(jobEntities.get(0).getId());
        assertJobFailed(importJob, 9, 6);
        List<String> possibleErrors = List.of(
                String.format(HAS_S_RELATIONSHIP_WITH_NON_EXISTING_PRODUCT_WITH_ID_S, SELLS, "660ecf92020e1b50f5698acf").toLowerCase(),
                String.format(HAS_S_RELATIONSHIP_WITH_NON_EXISTING_PRODUCT_WITH_ID_S, RELIESON, "660ecf92020e1b50f5698acf").toLowerCase(),
                String.format(HAS_S_RELATIONSHIP_WITH_NON_EXISTING_PRODUCT_WITH_ID_S, RELIESFROM, "660ecf92020e1b50f5698acf").toLowerCase(),
                MISSING_PRODUCT_IN_HIERARCHY.toLowerCase()
        );
        Set<String> actualErrors = importJob.getFailureReport().getProducts().stream()
                .map(JobReportProductRef::getFailReason)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Assertions.assertTrue(actualErrors.containsAll(possibleErrors), "Not all possible errors were encountered");
    }

    @Test
    void givenImportJob_withHierarchyWithInvalidProduct_whenRunningScheduler_thenHaveFailedProducts() throws Exception {
        List<JobEntity> jobEntities = createImportJob(null);
        String body = Files.readString(Paths.get(CONTRACT_1_CONTRACT_2_INVALID_JSON_PATH));
        uploadFileAndAssert(jobEntities.get(0).getId(), body);
        jobSchedulerService.executeScheduledJobs();
        ImportJob importJob = getImportById(jobEntities.get(0).getId());
        assertJobFailed(importJob, 6, 0);
        List<String> possibleErrors = List.of(
                INVALID_PRODUCT_MISSING_PRODUCT_OFFERING_AND_PRODUCT_SPECIFICATION,
                INVALID_PRODUCT_IN_HIERARCHY
        );
        Set<String> actualErrors = importJob.getFailureReport().getProducts().stream()
                .map(JobReportProductRef::getFailReason)
                .collect(Collectors.toSet());
        Assertions.assertTrue(actualErrors.containsAll(possibleErrors), "Not all possible errors were encountered");
    }

    @Test
    void givenImportJob_withHierarchyWithNoRoot_whenRunningScheduler_thenHaveFailedProducts() throws Exception {
        List<JobEntity> jobEntities = createImportJob(null);
        String body = Files.readString(Paths.get(CONTRACT_1_CONTRACT_2_NO_ROOT_JSON_PATH));
        uploadFileAndAssert(jobEntities.get(0).getId(), body);
        jobSchedulerService.executeScheduledJobs();
        ImportJob importJob = getImportById(jobEntities.get(0).getId());
        assertJobFailed(importJob, 15, 0);
        Assertions.assertTrue(importJob.getFailureReport().getProducts().stream()
                .map(JobReportProductRef::getFailReason)
                .allMatch(s -> s.equals(HIERARCHY_DOES_NOT_HAVE_A_ROOT_PRODUCT)), "Error not encountered");
    }

    @Test
    void givenImportJob_withHierarchyWithNoClearRoot_whenRunningScheduler_thenHaveFailedProducts() throws Exception {
        List<JobEntity> jobEntities = createImportJob(null);
        String body = Files.readString(Paths.get(CONTRACT_1_CONTRACT_2_NO_CLEAR_ROOT_JSON_PATH));
        uploadFileAndAssert(jobEntities.get(0).getId(), body);
        jobSchedulerService.executeScheduledJobs();
        ImportJob importJob = getImportById(jobEntities.get(0).getId());
        assertJobFailed(importJob, 15, 0);
        Assertions.assertTrue(importJob.getFailureReport().getProducts().stream()
                .map(JobReportProductRef::getFailReason)
                .allMatch(s -> s.equals(HIERARCHY_DOES_NOT_HAVE_A_CLEAR_ROOT_PRODUCT)), "Error not encountered");
    }

    public ImportJob getImportById(String jobId) throws Exception {
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_BY_ID_URI, jobId));
        resultActions.andExpect(status().isOk());
        return readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
    }

    private void uploadFileAndAssert(String importJobId, String body) throws Exception {
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_GET_UPLOAD_FILE_URL, importJobId));
        resultActions.andExpect(status().isOk());
        FileInformation fileInformation = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        Assertions.assertNotNull(fileInformation.getUrl(), "Pre-signed URL should not be null");
        uploadToS3(body, fileInformation.getUrl(), ContentType.APPLICATION_JSON.getMimeType());

        JobEntity jobEntityById = getJobEntityById(importJobId);
        awaitAndAssertFileCreated(s3Client, jobEntityById.getFileName());
        List<JobEntity> updatedJobEntities = getJobEntities(jobEntityById.getJobSpecification().getId());
        Assertions.assertNotNull(updatedJobEntities.get(0));
        assertEquals(jobEntityById.getFileName(), updatedJobEntities.get(0).getFileName());

    }

    @Test
    void givenNoFileAndImportJob_whenStartJob_thenJobNotStarted() throws Exception {
        List<JobEntity> jobEntities = createImportJob(null);
        jobSchedulerService.executeScheduledJobs();
        ImportJob importJob = getImportById(jobEntities.get(0).getId());
        Assertions.assertNotNull(importJob);
        assertEquals(JobStatusType.NOTSTARTED, importJob.getStatus());
    }

    @Test
    void givenImportJob_withExistingProducts_whenRunningScheduler_thenHaveFailedProducts() throws Exception {
        List<JobEntity> jobEntities = createImportJob(null);
        String body = Files.readString(Paths.get(CONTRACT_3_JSON_PATH));
        uploadFileAndAssert(jobEntities.get(0).getId(), body);
        jobSchedulerService.executeScheduledJobs();
        ImportJob importJob = getImportById(jobEntities.get(0).getId());
        assertJobSucceeded(importJob, body);
        jobEntities = createImportJob(null);
        uploadFileAndAssert(jobEntities.get(0).getId(), body);
        jobSchedulerService.executeScheduledJobs();
        importJob = getImportById(jobEntities.get(0).getId());
        assertJobFailed(importJob, 16, 0);
        Assertions.assertTrue(importJob.getFailureReport().getProducts().stream()
                .map(JobReportProductRef::getFailReason)
                .allMatch(s -> s.equals(PRODUCT_ALREADY_EXISTS_IN_DATABASE) || s.equals(HIERARCHY_HAS_PRODUCT_ALREADY_EXISTS_IN_DATABASE)), "Error not encountered");



    }

    private void assertJobSucceeded(ImportJob importJob, String body) throws JsonProcessingException {
        Assertions.assertNotNull(importJob);
        Assertions.assertNotNull(importJob.getFailureReport());
        Assertions.assertNotNull(importJob.getSuccessReport());
        assertEquals(0, importJob.getFailureReport().getProducts().size());
        List<Product> list = Arrays.asList((objectMapper.readValue(body, Product[].class)));
        assertEquals(list.size(), importJob.getSuccessReport().getProducts().size());
     }
}
