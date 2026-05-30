// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productinventory.config.AppConfig;
import com.orange.discobole.productinventory.converter.product.FormatConverterFactory;
import com.orange.discobole.productinventory.converter.product.StringToProductConverter;
import com.orange.discobole.productinventory.exception.FileNotReadyException;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobReportEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.repository.JobReportRepository;
import com.orange.discobole.productinventory.service.ImportJobService;
import com.orange.discobole.productinventory.service.ProductImportService;
import com.orange.discobole.productinventory.service.S3Service;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import static com.orange.discobole.productinventory.exception.model.BusinessErrors.IMPORT_JOB_MISSING_FILE;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.IMPORT_JOB_MISSING_HAS_EXPIRED;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class ImportJobServiceImpl implements ImportJobService {
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;
    private final ProductImportService productImportService;
    private final JobReportRepository jobReportRepository;
    private final AppConfig appConfig;
    private final FormatConverterFactory formatConverterFactory;

    @Override
    public void executeImport(JobSpecificationEntity jobSpecification, JobEntity job) throws IOException {
        boolean fileExists = s3Service.fileExists(job.getFileName());
        if (hasImportJobExpired(job, fileExists)) {
            throw new ProductInventoryException(IMPORT_JOB_MISSING_HAS_EXPIRED);
        }
        if (fileExists) {
            JobReportEntity jobReportEntity = JobReportEntity
                    .builder()
                    .jobId(job.getId())
                    .jobSpecificationId(job.getJobSpecification().getId())
                    .failedProducts(new ArrayList<>())
                    .succeededProducts(new ArrayList<>())
                    .build();
            StringToProductConverter stringConverter = formatConverterFactory.createStringToProductConverter(jobSpecification.getFileType());
            Path path = downloadFileFromS3(job.getFileName());
            productImportService.processProducts(stringConverter.convert(path), jobReportEntity);
            jobReportRepository.save(jobReportEntity);
            //TODO : delete the file here
        } else {
            throw new FileNotReadyException(IMPORT_JOB_MISSING_FILE);
        }
    }


    public Path downloadFileFromS3(String key) {
        Path localFilePath = Paths.get(appConfig.getTempPath(), key).normalize();

        try (ResponseInputStream<GetObjectResponse> s3InputStream = s3Service.getFile(key);
             OutputStream outputStream = Files.newOutputStream(localFilePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            byte[] buffer = new byte[8192]; // 8KB buffer to limit memory usage
            int bytesRead;
            while ((bytesRead = s3InputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return localFilePath;
        } catch (IOException | S3Exception e) {
            log.error("Failed to download file from S3: {}", key, e);
            throw new ProductInventoryException("Failed to download file from S3", e);
        }
    }

    private boolean hasImportJobExpired(JobEntity job, boolean fileExists) {
        //to do  check does fileexists=true if the upload is not yet finished in that case we're going to have to manually activate the job
        return job.getPlannedDate().isBefore(OffsetDateTime.now().minusHours(24)) && !fileExists;
    }
}
