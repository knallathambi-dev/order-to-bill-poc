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
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.util.AbstractJobsTest;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.*;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PRODUCT;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductBuilderWithRelation;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductSpecificationEntityBuilder;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Slf4j
class ExportProductApiTest extends AbstractJobsTest {

    @BeforeEach
    public void setUp() {
        createBucket(s3Client);
    }

    @AfterEach
    public void tearDown() {
        deleteBucketAndAllContents(s3Client);
    }

    @Test
    void givenNonEmptyDatabase_whenGetDirectExportWithStatus_thenExport() throws Exception {
        ProductEntity childProduct1 = mongoTemplate.save(
                createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false,
                        PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toString()).build());
        createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct1.getId(), false, "productOffering123", PRODUCT.getValue()).build();
        ProductEntity childProduct2 = mongoTemplate.save(
                createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false,
                        PRODUCT.getValue()).description(randomAlphabetic(10)).id(ObjectId.get().toString()).build());
        createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "Contract", childProduct2.getId(), false, "productOffering123", PRODUCT.getValue()).build();
        for (int i = 0; i < 3; i++) {
            mongoTemplate.save(getDefaultProductEntityBuilder().build());
        }
        Map<String, Object> filter = new HashMap<>();
        filter.put("status", new String[]{ProductStatusType.ACTIVE.getValue(), ProductStatusType.CREATED.getValue()});
        filter.put("contentType", ContentTypeEnum.JSON.getValue());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_EXPORT, param(filter));
        resultActions.andExpect(status().isOk());
        List<Product> exportedProducts = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertThat(exportedProducts).hasSize(5).allMatch(product -> product.getStatus() == (ProductStatusType.ACTIVE) || product.getStatus() == (ProductStatusType.CREATED));
    }


    @Test
    void givenNonEmptyDatabase_whenGetDirectExportWithTypeCsv_thenExport() throws Exception {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        Map<String, String> filter = new HashMap<>();
        filter.put("contentType", ContentTypeEnum.CSV.getValue());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_EXPORT, param(filter));
        resultActions.andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        resultActions.andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_TYPE, org.hamcrest.Matchers.containsString("text/csv"))
        );

        resultActions.andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString(".csv"))
        );

    }
    @Test
    void givenNonEmptyDatabase_whenGetDirectExportWithTypeJson_thenExport() throws Exception {
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        Map<String, String> filter = new HashMap<>();
        filter.put("contentType", ContentTypeEnum.JSON.getValue());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_EXPORT, param(filter));
        resultActions.andExpect(status().isOk());
        resultActions.andReturn().getResponse().getContentAsString();
        resultActions.andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_TYPE, org.hamcrest.Matchers.containsString("application/json")));
        resultActions.andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString(".json")));
    }

    @Test
    void fixOfRCEVulnerability_csvDirectExport() throws Exception {
        mongoTemplate.save(getDefaultProductEntityBuilder().name("=malicious code").build());
        Map<String, String> filter = new HashMap<>();
        filter.put("contentType", ContentTypeEnum.CSV.getValue());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_EXPORT, param(filter));
        resultActions.andExpect(status().isOk());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        List<Map<String, String>> csvContent = csvToListOfMaps(contentAsString);
        assertThat(csvContent).anyMatch(r -> r.get("name").equals("'" + "=malicious code"));
    }

    @Test
    void givenNonEmptyDatabase_whenCreateExportJobWithCsvContentType_thenValidFileCreated() throws Exception {
        mongoTemplate.save(getDefaultProductEntityBuilder().status(ProductStatusType.ABORTED).build());
        Map<String, String> filter = new HashMap<>();
        filter.put("contentType", ContentTypeEnum.JSON.getValue());
        ResultActions resultActions = createExportJob(
                "status=CREATED",
                ContentTypeEnum.CSV,
                ImmediateJobScheduler
                        .builder()
                        .build(),
                List.of("id", "status", "description"));
        resultActions.andExpect(status().isCreated());
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        ProductEntity expected = mongoTemplate.save(getDefaultProductEntityBuilder().build());
        jobSchedulerService.executeScheduledJobs();
        JobEntity byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        awaitAndAssertExpectedWithCsvContentType(s3Client, byId, expected);
    }

    @Test
    void givenNonEmptyDatabase_whenCreateExportJobWithCsvContentType_thenValidFileFormatCreated() throws Exception {
        mockCatalogUrl(PRODUCT_SPECIFICATION_URL, VALID_PRODUCT_SPECIFICATION_ID, PRODUCT_SPECIFICATION_JSON, HttpStatus.OK.value());
        mockCatalogUrl(PRODUCT_OFFERING_URL, VALID_PRODUCT_OFFERING_CONTRACT_ID, PRODUCT_OFFERING_JSON, HttpStatus.OK.value());
        Product product = createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(VALID_PRODUCT_OFFERING_CONTRACT_ID, VALID_PRODUCT_SPECIFICATION_ID, VALID_BUNDLE_PRODUCT_OFFERING_ID, VALID_ATOMIC_PRODUCT_OFFERING_ID).build();
        ResultActions createProductResultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT, contentBody(product), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        createProductResultActions.andExpect(status().isCreated());
        ResultActions resultActions = createExportJob(

                "status=CREATED",
                ContentTypeEnum.CSV,
                ImmediateJobScheduler
                        .builder()
                        .build(), List.of("id", "status", "description")
        );
        resultActions.andExpect(status().isCreated());
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        jobSchedulerService.executeScheduledJobs();
        JobEntity byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        awaitAndAssertExpectedWithCsvContentType(s3Client, byId, mongoTemplate.findAll(ProductEntity.class).toArray(new ProductEntity[0]));
    }


    @ParameterizedTest
    @MethodSource("queryAndExpectedProvider")
    void givenNonEmptyDatabase_whenCreateExportJobWithDifferentQueries_thenFileCreated(String query, List<ProductEntity> notExpected, List<ProductEntity> expected) throws Exception {
        for (ProductEntity product : notExpected) {
            mongoTemplate.save(product);
        }
        ResultActions resultActions = createExportJob(
                query,
                ContentTypeEnum.JSON,
                ImmediateJobScheduler
                        .builder()
                        .build(), List.of("id", "status", "description"));
        resultActions.andExpect(status().isCreated());
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        ProductEntity[] array = expected.stream()
                .map(mongoTemplate::save)
                .toArray(ProductEntity[]::new);
        jobSchedulerService.executeScheduledJobs();
        JobEntity byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        awaitAndAssertExpected(s3Client, byId, array);
        ResultActions exportFileInformationresultActions =
                callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_DOWNLOAD_LINK_URI, byId.getId()));
        exportFileInformationresultActions.andExpect(status().isOk());
        assertExportFileInformation(exportFileInformationresultActions);
    }
    @Test
    void givenNonEmptyDatabase_whenCreateExportJobWithLastUpdateDateGteQuery_thenFileCreated() throws Exception {

        // Save the initial product entity and wait for 2 seconds
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        Thread.sleep(2000);
        OffsetDateTime twoSecondsLater = OffsetDateTime.now().minusSeconds(1);
        ProductEntity expected = mongoTemplate.save(getDefaultProductEntityBuilder().build());
        List<ProductEntity> expectedList = new ArrayList<>();
        expectedList.add(expected);
        // Create the export query with the lastUpdateDateGte value
        String query = "lastUpdateDate.gte=" + dateFormatter.format(twoSecondsLater);

        // Create the export jobSpecification
        ResultActions resultActions = createExportJob(
                query,
                ContentTypeEnum.JSON,
                ImmediateJobScheduler
                        .builder()
                        .build(), List.of("id", "status", "description"));
        resultActions.andExpect(status().isCreated());

        // Parse the export job from the response
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        // Find the jobSpecification entity by ID
        JobEntity byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        assertNotNull(byId);

        // Convert the expected list to an array
        ProductEntity[] array = expectedList.toArray(new ProductEntity[0]);

        // Execute the export jobSpecification
        jobSchedulerService.executeScheduledJobs();
        byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        // Await and assert that the expected entities are in the S3 bucket
        awaitAndAssertExpected(s3Client, byId, array);
    }
    @Test
    void givenNonEmptyDatabase_whenCreateExportJobWithLastUpdateDateLteQuery_thenFileCreated() throws Exception {

        // Save the initial product entity
        ProductEntity expected = mongoTemplate.save(getDefaultProductEntityBuilder().build());
        List<ProductEntity> expectedList = new ArrayList<>();
        expectedList.add(expected);

        // Wait 2 seconds
        Thread.sleep(2000);

        // Set the lastUpdateDateLte to 2 seconds ago
        OffsetDateTime twoSecondsAgo = OffsetDateTime.now().minusSeconds(2);

        // Create the export query with the lastUpdateDateLte value
        String query = "lastUpdateDate.lte=" + dateFormatter.format(twoSecondsAgo);
        // Save another product entity that should not be included in the result
        mongoTemplate.save(getDefaultProductEntityBuilder().build());

        // Create the export jobSpecification
        ResultActions resultActions = createExportJob(
                query,
                ContentTypeEnum.JSON,
                ImmediateJobScheduler
                        .builder()
                        .build(), List.of("id", "status", "description"));
        resultActions.andExpect(status().isCreated());

        // Parse the export job from the response
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        JobEntity byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        assertNotNull(byId);

        // Convert the expected list to an array
        ProductEntity[] array = expectedList.toArray(new ProductEntity[0]);

        // Execute the export jobSpecification
        jobSchedulerService.executeScheduledJobs();
        byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        // Await and assert that the expected entities are in the S3 bucket
        awaitAndAssertExpected(s3Client, byId, array);
    }

    @Test
    void givenNonEmptyDatabase_whenCreateExportJobWithLastUpdateDateBetweenQuery_thenFileCreated() throws Exception {

        // Save the first product entity
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        Thread.sleep(2000);

        // Save the second product entity
        ProductEntity expected = mongoTemplate.save(getDefaultProductEntityBuilder().build());
        List<ProductEntity> expectedList = new ArrayList<>();
        expectedList.add(expected);
        Thread.sleep(2000);

        // Save the third product entity
        mongoTemplate.save(getDefaultProductEntityBuilder().build());

        // Set the lastUpdateDateGte to the time of the first entity creation plus 2 seconds
        OffsetDateTime twoSecondsAfterEntity1 = OffsetDateTime.now().minusSeconds(3);

        // Set the lastUpdateDateLte to the time of the second entity creation plus 2 seconds
        OffsetDateTime twoSecondsAfterEntity2 = OffsetDateTime.now().minusSeconds(1);

        // Create the export query with the lastUpdateDateGte and lastUpdateDateLte values

        String query = "lastUpdateDate.lte=" + dateFormatter.format(twoSecondsAfterEntity2)
                + "&lastUpdateDate.gte=" + dateFormatter.format(twoSecondsAfterEntity1);

        // Create the export jobSpecification
        ResultActions resultActions = createExportJob(
                query,
                ContentTypeEnum.JSON,
                ImmediateJobScheduler
                        .builder()
                        .build(), List.of("id", "status", "description"));
        resultActions.andExpect(status().isCreated());

        // Parse the export job from the response
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        // Find the jobSpecification entity by ID
        JobEntity byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        assertNotNull(byId);

        // Convert the expected list to an array
        ProductEntity[] array = expectedList.toArray(new ProductEntity[0]);

        // Execute the export jobSpecification
        jobSchedulerService.executeScheduledJobs();
        byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        // Await and assert that the expected entities are in the S3 bucket
        awaitAndAssertExpected(s3Client, byId, array);
    }

    @Test
    void givenNonEmptyDatabase_whenCreateExportJobWithImmediateType_thenFileCreated() throws Exception {
        ProductEntity expected = getDefaultProductEntityBuilder().build();
        expected = mongoTemplate.save(expected);
        ResultActions resultActions = createExportJob(
                "",
                ContentTypeEnum.JSON,
                ImmediateJobScheduler
                        .builder()
                        .build(), List.of("id", "status", "description"));
        resultActions.andExpect(status().isCreated());
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        JobEntity byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        assert byId != null;
        jobSchedulerService.executeScheduledJobs();
        byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        awaitAndAssertExpected(s3Client, byId, expected);
        ResultActions exportFileInformationresultActions =
                callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_DOWNLOAD_LINK_URI, byId.getId()));
        exportFileInformationresultActions.andExpect(status().isOk());
        assertExportFileInformation(exportFileInformationresultActions);
    }

    @Test
    void givenNonEmptyDatabase_whenCreateExportJobWithFields_thenFieldsReturned() throws Exception {
        ProductEntity expected = getDefaultProductEntityBuilder().build();
        expected = mongoTemplate.save(expected);
        List<String> fields = List.of("status", "description");
        ResultActions resultActions = createExportJob(
                "",
                ContentTypeEnum.JSON,
                ImmediateJobScheduler
                        .builder()
                        .build(),
                fields);
        resultActions.andExpect(status().isCreated());
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        JobEntity byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        assert byId != null;
        jobSchedulerService.executeScheduledJobs();
        byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        awaitAndAssertExpected(s3Client, byId, fields, expected);
        ResultActions exportFileInformationresultActions =
                callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_DOWNLOAD_LINK_URI, byId.getId()));
        exportFileInformationresultActions.andExpect(status().isOk());
        JobSpecificationEntity parentJob = mongoTemplate.findById(exportJob.getId(), JobSpecificationEntity.class);
        Assertions.assertNotNull(parentJob);
        Assertions.assertNotNull(parentJob.getActivePeriod());

        assertExportFileInformation(exportFileInformationresultActions);


    }

    @Test
    void givenNonEmptyDatabase_whenCreateExportJob_thenJobSpecificationStatusChangeIsNotEmpty() throws Exception {
        ProductEntity expected = getDefaultProductEntityBuilder().build();
        expected = mongoTemplate.save(expected);
        ResultActions resultActions = createExportJob(
                "",
                ContentTypeEnum.JSON,
                ImmediateJobScheduler
                        .builder()
                        .build(), List.of("id", "status", "description"));
        resultActions.andExpect(status().isCreated());
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        JobEntity byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        assert byId != null;
        jobSchedulerService.executeScheduledJobs();
        byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        awaitAndAssertExpected(s3Client, byId, expected);
        ResultActions exportFileInformationresultActions =
                callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_DOWNLOAD_LINK_URI, byId.getId()));
        exportFileInformationresultActions.andExpect(status().isOk());

        assertExportFileInformation(exportFileInformationresultActions);
        JobSpecificationEntity jobSpecificationEntity = mongoTemplate.findById(exportJob.getId(), JobSpecificationEntity.class);
        assert jobSpecificationEntity != null;
        Assertions.assertNotNull(jobSpecificationEntity.getLifeCycleStatusChange());
        Assertions.assertEquals(3, jobSpecificationEntity.getLifeCycleStatusChange().size());

    }

    private void assertExportFileInformation(ResultActions exportFileInformationresultActions) throws JsonProcessingException, UnsupportedEncodingException {
        ExportFileInformation exportFileInformation = objectMapper.readValue(exportFileInformationresultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertNotNull(exportFileInformation.getUrl());
        Assertions.assertNotNull(exportFileInformation.getContentType());
        Assertions.assertNotNull(exportFileInformation.getCompletionDate());
        Assertions.assertNotNull(exportFileInformation.getValidFor());
        Assertions.assertNotNull(exportFileInformation.getValidFor().getStartDateTime());
        Assertions.assertNotNull(exportFileInformation.getValidFor().getEndDateTime());
    }

    @Test
    void fixOfRCEVulnerability_csvExportUsingExportJob() throws Exception {
        ProductEntity expectedProductWithVulnerableProperty = mongoTemplate.save(getDefaultProductEntityBuilder().name("=malicious code").build());

        ResultActions resultActions = createExportJob(
                "status=Created",
                ContentTypeEnum.CSV,
                ImmediateJobScheduler
                        .builder()
                        .build(),
                List.of("id", "name", "status", "description"));
        resultActions.andExpect(status().isCreated());

        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        jobSchedulerService.executeScheduledJobs();
        JobEntity executedJob = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );

        awaitAndAssertExpectedWithCsvContentType(s3Client, executedJob, csvContent -> {
            List<ProductEntity> expectedProducts = List.of(expectedProductWithVulnerableProperty);

            assertFalse(csvContent.isEmpty(), "The content of the file should not be empty");
            log.info("CSV content successfully read: {}", csvContent);

            List<Map<String, String>> actualCsvContent = csvToListOfMaps(csvContent);

            assertThat(actualCsvContent).hasSize(expectedProducts.size());

            for (ProductEntity expected : expectedProducts) {
                assertThat(actualCsvContent).anyMatch(row -> row.get("id").equals(expected.getId()) && row.get("name").equals("'" + expected.getName()));
            }
        });
    }

    private List<Map<String, String>> csvToListOfMaps(String csv) {
        List<Map<String, String>> list = new ArrayList<>();
        String[] lines = csv.split("\\n");
        if (lines.length < 2) {
            return list;
        }

        String[] keys = Arrays.stream(lines[0].split(";")).map(c -> c.replaceAll("^\"|\"$", "")).toArray(String[]::new);
        for (int i = 1; i < lines.length; i++) {
            String[] values = Arrays.stream(lines[i].split(";")).map(c -> c.replaceAll("^\"|\"$", "")).toArray(String[]::new);
            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < keys.length && j < values.length; j++) {
                map.put(keys[j], values[j]);
            }
            list.add(map);
        }
        return list;
    }
}
