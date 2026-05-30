// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.RelatedPartyEntity;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.service.impl.JobSchedulerServiceImpl;
import com.orange.discobole.productinventory.util.creator.JobSpecificationCreator;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.CollectionUtils;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.testutils.Waiter;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.assertions.Assertions.assertTrue;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PRODUCT;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductSpecificationEntityBuilder;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.http.HttpMethod.POST;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Slf4j
public abstract class AbstractJobsTest extends AbstractTest {
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    @Autowired
    protected S3Client s3Client;
    @Autowired
    protected JobSchedulerServiceImpl jobSchedulerService;
    @Value("${config.aws.s3.bucketName}")
    private String bucketName;

    static Stream<Arguments> queryAndExpectedProvider() {
        // Define the date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        OffsetDateTime sampleDate = OffsetDateTime.parse("2024-05-24T13:46:09Z", formatter);

        // Define the test data for different fields
        return Stream.of(
                // Test for status field
                Arguments.of(
                        "status=CREATED&status=ACTIVE",
                        List.of(
                                getDefaultProductEntityBuilder().status(ProductStatusType.ABORTED).build()
                        ),
                        List.of(
                                getDefaultProductEntityBuilder().status(ProductStatusType.CREATED).build(),
                                getDefaultProductEntityBuilder().status(ProductStatusType.ACTIVE).build()
                        )
                ),
                // Test for relatedParty.partyOrPartyRole.id field
                Arguments.of(
                        "relatedParty.partyOrPartyRole.id=1",
                        List.of(
                                getDefaultProductEntityBuilder().relatedParty(List.of(RelatedPartyEntity.builder().id("2").build())).build()
                        ),
                        List.of(
                                getDefaultProductEntityBuilder().relatedParty(List.of(RelatedPartyEntity.builder().id("1").build())).build()
                        )
                ),
                // Test for startDate.gte field
                Arguments.of(
                        "startDate.gte=" + sampleDate.format(formatter),
                        List.of(
                                getDefaultProductEntityBuilder().startDate(sampleDate.minusDays(1)).build()
                        ),
                        List.of(
                                getDefaultProductEntityBuilder().startDate(sampleDate.plusDays(1)).build()
                        )
                ),
                // Test for startDate.lte field
                Arguments.of(
                        "startDate.lte=" + sampleDate.format(formatter),
                        List.of(
                                getDefaultProductEntityBuilder().startDate(sampleDate.plusDays(1)).build()
                        ),
                        List.of(
                                getDefaultProductEntityBuilder().startDate(sampleDate.minusDays(1)).build()
                        )
                ),
                // Test for creationDate.gte field
                Arguments.of(
                        "creationDate.gte=" + sampleDate.format(formatter),
                        List.of(
                                getDefaultProductEntityBuilder().creationDate(sampleDate.minusDays(1)).build()
                        ),
                        List.of(
                                getDefaultProductEntityBuilder().creationDate(sampleDate.plusDays(1)).build()
                        )
                ),
                // Test for creationDate.lte field
                Arguments.of(
                        "creationDate.lte=" + sampleDate.format(formatter),
                        List.of(
                                getDefaultProductEntityBuilder().creationDate(sampleDate.plusDays(1)).build()
                        ),
                        List.of(
                                getDefaultProductEntityBuilder().creationDate(sampleDate.minusDays(1)).build()
                        )
                ),
                // Test for startDate between startDate1 and startDate3
                Arguments.of(
                        "startDate.gte=" + sampleDate.format(formatter) +
                                "&startDate.lte=" + sampleDate.plusDays(2).format(formatter),
                        List.of(
                                getDefaultProductEntityBuilder().startDate(sampleDate.minusDays(1)).build()
                        ),
                        List.of(
                                getDefaultProductEntityBuilder().startDate(sampleDate.plusDays(1)).build()
                        )
                ),
                // Test for creationDate between creationDate1 and creationDate3
                Arguments.of(
                        "creationDate.gte=" + sampleDate.format(formatter) +
                                "&creationDate.lte=" + sampleDate.plusDays(2).format(formatter),
                        List.of(
                                getDefaultProductEntityBuilder().creationDate(sampleDate.minusDays(1)).build()
                        ),
                        List.of(
                                getDefaultProductEntityBuilder().creationDate(sampleDate.plusDays(1)).build()
                        )
                )
        );
    }

    protected static ProductEntity.ProductEntityBuilder getDefaultProductEntityBuilder() {
        return createProductSpecificationEntityBuilder(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue());
    }

    protected ResultActions createExportJob(String query, ContentTypeEnum contentTypeEnum, JobScheduler jobSchedule, List<String> fields) {
        String exportJob = toJsonString(
                ExportJobSpecification
                        .builder()
                        .atType("ExportJobSpecification")
                        .query(query)
                        .contentType(contentTypeEnum)
                        .fields(fields)
                        .build()
                        .schedule(jobSchedule));
        return callRestfulEndpoint(
                mockMvc,
                POST,
                PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION,
                contentBodyJson(exportJob),
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

    }

    protected void createBucket(S3Client s3) {
        String testBucket =
                s3.listBuckets()
                        .buckets()
                        .stream()
                        .map(Bucket::name)
                        .filter(name -> name.equals(bucketName))
                        .findAny()
                        .orElse(null);

        if (testBucket == null) {
            s3.createBucket(r -> r.bucket(bucketName));
            Waiter.run(() -> s3.headBucket(r -> r.bucket(bucketName)))
                    .ignoringException(NoSuchBucketException.class)
                    .orFail();
        }
    }

    protected void deleteBucketAndAllContents(S3Client s3) {
        try {
            log.info("Deleting S3 bucket: " + bucketName);
            ListObjectsResponse response = Waiter.run(() -> s3.listObjects(r -> r.bucket(bucketName)))
                    .ignoringException(NoSuchBucketException.class)
                    .orFail();
            List<S3Object> objectListing = response.contents();

            if (objectListing != null) {
                while (true) {
                    for (Iterator<?> iterator = objectListing.iterator(); iterator.hasNext(); ) {
                        S3Object objectSummary = (S3Object) iterator.next();
                        s3.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(objectSummary.key()).build());
                    }

                    if (response.isTruncated()) {
                        objectListing = s3.listObjects(ListObjectsRequest.builder()
                                        .bucket(bucketName)
                                        .marker(response.marker())
                                        .build())
                                .contents();
                    } else {
                        break;
                    }
                }
            }

            ListObjectVersionsResponse versions = s3
                    .listObjectVersions(ListObjectVersionsRequest.builder().bucket(bucketName).build());

            if (versions.deleteMarkers() != null) {
                versions.deleteMarkers().forEach(v -> s3.deleteObject(DeleteObjectRequest.builder()
                        .versionId(v.versionId())
                        .bucket(bucketName)
                        .key(v.key())
                        .build()));
            }

            if (versions.versions() != null) {
                versions.versions().forEach(v -> s3.deleteObject(DeleteObjectRequest.builder()
                        .versionId(v.versionId())
                        .bucket(bucketName)
                        .key(v.key())
                        .build()));
            }

            s3.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("Failed to delete bucket: " + bucketName, e);
        }
    }

    protected void awaitAndAssertExpected(S3Client s3Client, JobEntity jobSpecification, ProductEntity... expectedProducts) {
        awaitAndAssertExpected(s3Client, jobSpecification, List.of(), expectedProducts);
    }

    protected void awaitAndAssertExpected(S3Client s3Client, JobEntity jobSpecification, List<String> fields, ProductEntity... expectedProducts) {
        await().atMost(120, SECONDS).untilAsserted(() -> {
            ResponseInputStream<GetObjectResponse> object = null;
            try {
                object = s3Client.getObject(GetObjectRequest.builder().key(jobSpecification.getFileName()).bucket(bucketName).build());
                assertNotNull(object);

                String content;
                if (jobSpecification.getFileName().endsWith(".gz")) {
                    try (GZIPInputStream gzipInputStream = new GZIPInputStream(object)) {
                        content = IOUtils.toString(gzipInputStream, StandardCharsets.UTF_8);
                    }
                } else {
                    content = IOUtils.toString(object, StandardCharsets.UTF_8);
                }

                assertFalse(content.isEmpty(), "The content of the file should not be empty");

                if (!CollectionUtils.isEmpty(fields)) {
                    for (String column : fields) {
                        assertTrue(content.contains("\"" + column + "\""));
                    }
                }

                List<Product> exportedProducts = objectMapper.readValue(content, new TypeReference<>() {});
                assertThat(exportedProducts).hasSize(expectedProducts.length);

                for (ProductEntity expected : expectedProducts) {
                    assertThat(exportedProducts).anyMatch(product -> product.getId().equals(expected.getId()));
                }

            } catch (NoSuchKeyException e) {
                log.error("The specified key does not exist: " + jobSpecification.getFileName());
                assert false;
            } catch (IOException e) {
                log.error("An error occurred while reading the S3 object content", e);
                assert false;
            } finally {
                if (object != null) {
                    try {
                        object.close();
                    } catch (IOException e) {
                        log.error("Failed to close the S3 object input stream", e);
                    }
                }
            }
        });
    }

    protected void awaitAndAssertExpectedWithCsvContentType(S3Client s3Client, JobEntity jobSpecification, ProductEntity... expectedProducts) {
        awaitAndAssertExpectedWithCsvContentType(s3Client, jobSpecification, csvContent -> {
            assertFalse(csvContent.isEmpty(), "The content of the file should not be empty");
            log.info("CSV content successfully read: {}", csvContent);

            List<String> list = Arrays.stream(csvContent.split("\n")) // Use '\n' as default separator
                    .map(s -> s.split(";")[0]) // Extract first column (ID)
                    .filter(s -> !s.isEmpty()) // Remove empty rows
                    .collect(Collectors.toList());

            assertThat(list).hasSize(expectedProducts.length + 1);

            for (ProductEntity expected : expectedProducts) {
                assertThat(list).anyMatch(id -> id.replace("\"", "").equals(expected.getId()));
            }
        });
    }

    protected void awaitAndAssertExpectedWithCsvContentType(S3Client s3Client, JobEntity jobSpecification, Consumer<String> csvAssertor) {
        await().atMost(120, SECONDS).untilAsserted(() -> {
            ResponseInputStream<GetObjectResponse> object = null;
            try {
                object = s3Client.getObject(GetObjectRequest.builder().key(jobSpecification.getFileName()).bucket(bucketName).build());
                assertNotNull(object);
                String csv;
                if (jobSpecification.getFileName().toLowerCase().endsWith(".gz")) {
                    log.info("Processing GZIP file: {}", jobSpecification.getFileName());
                    try (GZIPInputStream gzipInputStream = new GZIPInputStream(object)) {
                        csv = IOUtils.toString(gzipInputStream, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        log.error("Failed to decompress GZIP file", e);
                        throw new RuntimeException("Failed to decompress GZIP file: " + jobSpecification.getFileName(), e);
                    }
                } else {
                    log.info("Processing non-GZIP file: {}", jobSpecification.getFileName());
                    csv = IOUtils.toString(object, StandardCharsets.UTF_8);
                }

                csvAssertor.accept(csv);

            } catch (NoSuchKeyException e) {
                log.error("The specified key does not exist: {}", jobSpecification.getFileName());
                throw new RuntimeException("The specified key does not exist: " + jobSpecification.getFileName(), e);
            } catch (IOException e) {
                log.error("An error occurred while reading the S3 object content", e);
                throw new RuntimeException("Error while reading S3 object content: " + jobSpecification.getFileName(), e);
            } finally {
                if (object != null) {
                    try {
                        object.close();
                    } catch (IOException e) {
                        log.error("Failed to close the S3 object input stream", e);
                    }
                }
            }
        });
    }

    public static void uploadToS3(List<Product> list, String url, String contentType) throws IOException {
        uploadToS3(toJsonString(list), url, contentType);
    }

    public static void uploadToS3(String body, String url, String contentType) throws IOException {
        disableSSLCertificateValidation();
        byte[] jsonBytes = body.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Content-Length", String.valueOf(jsonBytes.length));

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBytes);
        }

        int responseCode = connection.getResponseCode();
        Assertions.assertEquals(200, responseCode, "JSON upload to S3 should be successful");
    }


    // Disable SSL validation (use only in test environments)
    private static void disableSSLCertificateValidation() {
        try {
            TrustManager[] trustAllCertificates = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) { //NOSONAR
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) { //NOSONAR
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public List<JobEntity> createImportJob(OffsetDateTime plannedDate, ContentTypeEnum contentType) {
        String jobSpecification;
        if (plannedDate == null) {
            jobSpecification = toJsonString(JobSpecificationCreator.createImportJobSpecificationBuilderWithImmediateJobScheduler(contentType).build());

        } else {
            jobSpecification = toJsonString(JobSpecificationCreator
                    .createImportJobSpecificationBuilderWithOneTimeJobScheduler(plannedDate).build());
        }
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        ImportJobSpecification importJobSpecification = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        List<JobEntity> jobEntities = getJobEntities(importJobSpecification.getId());
        Assertions.assertNotNull(jobEntities);
        Assertions.assertNotNull(jobEntities.get(0));
        Assertions.assertNotNull(jobEntities.get(0).getId());
        return jobEntities;
    }
    public List<JobEntity> createImportJob(OffsetDateTime plannedDate) {
        return createImportJob(plannedDate, ContentTypeEnum.JSON);
    }

    public List<JobEntity> getJobEntities(String importJobSpecification) {
        Query query = new Query();
        query.addCriteria(Criteria.where(JobEntity.Fields.jobSpecification + "." + JobSpecificationEntity.Fields.id).is(importJobSpecification));
        return mongoTemplate.find(query, JobEntity.class);
    }

    public JobEntity getJobEntityById(String jobId) {
        return mongoTemplate.findById(jobId, JobEntity.class);
    }

    protected void awaitAndAssertFileCreated(S3Client s3Client, String fileName) {
        await().atMost(120, SECONDS).untilAsserted(() -> {
            ResponseInputStream<GetObjectResponse> object;
            try {
                object = s3Client.getObject(GetObjectRequest.builder().key(fileName).bucket("cpib").build());
                Assertions.assertNotNull(object);
                String content = org.apache.commons.io.IOUtils.toString(object, StandardCharsets.UTF_8);
                assertFalse(content.isEmpty(), "The content of the file should not be empty");
            } catch (NoSuchKeyException e) {
                log.error("The specified key does not exist: " + fileName);
            }
        });
    }
}
