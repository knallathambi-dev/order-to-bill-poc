// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.config.AppConfig;
import com.orange.discobole.productinventory.converter.product.FormatConverter;
import com.orange.discobole.productinventory.converter.product.FormatConverterFactory;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.exception.UnsupportedTypeException;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.model.job.enumerate.FileType;
import com.orange.discobole.productinventory.service.ExportProductService;
import com.orange.discobole.productinventory.service.FilterQueryService;
import com.orange.discobole.productinventory.service.S3Service;
import com.orange.discobole.productinventory.util.FileCompressor;
import com.orange.discobole.productinventory.util.FileUtils;
import com.orange.discobole.productinventory.validation.pageable.impl.ProductFieldFetcher;
import com.orange.discobole.productinventory.validation.query.CompositeValidator;
import com.orange.discobole.productinventory.validation.query.ProductQueryValidator;
import com.orange.discobole.productinventory.validation.query.QueryStringValidator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.CONFLICT;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INTERNAL_ERROR;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.CANNOT_DOWNLOAD_FILE;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.FAILED_TO_PROCESS_PRODUCT;
import static com.orange.discobole.productinventory.mapper.JobSpecificationMapper.convertFileTypeToContentType;
import static com.orange.discobole.productinventory.util.ApiUtil.getQueryMap;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class ExportProductServiceImpl implements ExportProductService {
    private final FilterQueryService filterQueryService;
    private final MongoOperations mongoOperations;
    private final S3Service s3Service;
    private final AppConfig appConfig;
    private final FormatConverterFactory formatConverterFactory;

    private ExecutorService executorService;
    @Value("${config.export.limit}")
    private int exportLimit;

    @PostConstruct
    public void exportProductService() {
        try {
            Files.createDirectories(Paths.get(appConfig.getTempPath()));
        } catch (IOException e) {
            throw new ProductInventoryException("Could not create upload folder!");
        }
        executorService = Executors.newFixedThreadPool(10);
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }


    @Override
    public ExportJobSpecification createExportJob(ExportJobSpecification exportJob) {
        if (exportJob == null) {
            return null;
        }
        CompositeValidator
                .builder()
                .addValidator(new QueryStringValidator())
                .addValidator(new ProductQueryValidator())
                .build()
                .validate(exportJob.getQuery());

        validateFields(exportJob.getFields());
        exportJob.setCreationDate(OffsetDateTime.now());
        exportJob.setLifecycleStatus(JobSpecificationStatusType.CREATED);
        if (exportJob.getContentType() == null) {
            exportJob.setContentType(ContentTypeEnum.JSON);
        }
        return exportJob;
    }

    private void validateFields(List<String> fields) throws IllegalArgumentException {
        if (!CollectionUtils.isEmpty(fields)) {
            filterQueryService.validateFieldsToFetch(new ProductFieldFetcher(), fields.toArray(new String[0]));
        }

    }

    @SneakyThrows
    @Override
    public void exportProducts(MultiValueMap<String, Object> filters, PipedInputStream inputStream, ContentTypeEnum contentType) {
        int queryLimit = exportLimit / AVERAGE_PRODUCT_SIZE;
        Query query = filterQueryService.createQuery(filters);
        query.limit(queryLimit * QUERY_LIMIT_MULTIPLIER);
        PipedOutputStream pipedOutputStream = new PipedOutputStream(inputStream);
        executorService.submit(() -> {
            try (OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(pipedOutputStream), StandardCharsets.UTF_8); Stream<ProductEntity> productStream = mongoOperations.stream(query, ProductEntity.class)) {
                formatConverterFactory.createConverter(FileType.fromValue(contentType)).convert(productStream, writer);
            } catch (IOException e) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INTERNAL_ERROR.getCode(), INTERNAL_ERROR.getStatus(), FAILED_TO_PROCESS_PRODUCT);
            }
        });
    }
    @Override
    public String exportProducts(JobSpecificationEntity jobSpecification, String fileName) throws IOException {
        MultiValueMap<String, Object> queryMap = getQueryMap(jobSpecification);
        Query query = filterQueryService.createQuery(queryMap);

        if (!CollectionUtils.isEmpty(jobSpecification.getFields())) {
            String[] strings = filterQueryService.extractFields(
                    String.join(",", jobSpecification.getFields()),
                    REQUIRED_PRODUCT_FIELDS.toArray(new String[0])
            );
            query.fields().include(strings);
        }
        Path compressedFilePath;
        try (PipedInputStream pipedInputStream = new PipedInputStream();
             PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream)) {
            executorService.submit(() -> exportProductsToPipe(query, pipedOutputStream, jobSpecification));
            Path path = FileUtils.writePipeOutputStreamToFile(pipedInputStream, appConfig.getTempPath(), jobSpecification.getFileType());
            compressedFilePath = FileCompressor.compressFileToGzip(path);
            s3Service.uploadFile(compressedFilePath, compressedFilePath.getFileName().toString());
            FileUtils.deleteFile(path);


        } catch (IOException e) {
            log.error("Error during exportProducts", e);
            throw e;
        }
        return compressedFilePath.getFileName().toString();
    }

    @Override
    public ExportFileInformation getExportFileInformation(JobEntity jobEntity, JobSpecificationEntity jobSpecificationEntity) {
        if (!JobTypeEnum.EXPORTJOB.equals(jobEntity.getAtType())) {
            throw new UnsupportedTypeException(jobEntity.getAtType());
        }
        if (!JobStatusType.SUCCEEDED.equals(jobEntity.getStatus())) {
            throw new ProductInventoryException(HttpStatus.CONFLICT, CONFLICT.getCode(), CONFLICT.getStatus(), String.format(CANNOT_DOWNLOAD_FILE, jobEntity.getStatus()));
        }
        PresignedGetObjectRequest presignedGetObjectRequest = s3Service.generatePresignedDownloadUrl(jobEntity.getFileName());
        return ExportFileInformation
                .builder()
                .url(presignedGetObjectRequest
                        .url()
                        .toString()
                )
                .completionDate(jobEntity
                        .getExecutionPeriod()
                        .getEndDateTime()
                )
                .contentType(
                        convertFileTypeToContentType(jobSpecificationEntity.getFileType())
                )
                .validFor(TimePeriod
                        .builder()
                        .startDateTime(OffsetDateTime.now())
                        .endDateTime(presignedGetObjectRequest
                                .expiration()
                                .atOffset(OffsetDateTime
                                        .now()
                                        .getOffset())
                        )
                        .build())
                .build();

    }


    private void exportProductsToPipe(Query query, PipedOutputStream pipedOutputStream, JobSpecificationEntity jobSpecification) {
        FormatConverter converter;
        if (!CollectionUtils.isEmpty(jobSpecification.getFields())) {
            Set<String> fields = jobSpecification.getFields();
            fields.addAll(REQUIRED_PRODUCT_FIELDS);
            converter = formatConverterFactory.createConverter(jobSpecification.getFileType(), fields);
        } else {
            converter = formatConverterFactory.createConverter(jobSpecification.getFileType());
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(pipedOutputStream), StandardCharsets.UTF_8); Stream<ProductEntity> productStream = mongoOperations.stream(query, ProductEntity.class)) {
            converter.convert(productStream, writer);
        } catch (IOException e) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INTERNAL_ERROR.getCode(), INTERNAL_ERROR.getStatus(), FAILED_TO_PROCESS_PRODUCT);
        }
    }


}