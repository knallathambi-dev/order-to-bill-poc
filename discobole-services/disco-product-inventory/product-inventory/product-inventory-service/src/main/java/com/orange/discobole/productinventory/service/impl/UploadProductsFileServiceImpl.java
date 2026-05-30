// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.dto.v1.FileInformation;
import com.orange.discobole.productinventory.dto.v1.JobStatusType;
import com.orange.discobole.productinventory.dto.v1.JobTypeEnum;
import com.orange.discobole.productinventory.dto.v1.TimePeriod;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.exception.UnsupportedTypeException;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.service.JobService;
import com.orange.discobole.productinventory.service.JobSpecificationService;
import com.orange.discobole.productinventory.service.S3Service;
import com.orange.discobole.productinventory.service.UploadProductsFileService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.*;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static com.orange.discobole.productinventory.mapper.JobSpecificationMapper.convertFileTypeToContentType;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class UploadProductsFileServiceImpl implements UploadProductsFileService {
    private final S3Service s3Service;
    private final JobService jobService;
    private final JobSpecificationService jobSpecificationService;
    @Override
    public void uploadFile(MultipartFile file, JobEntity jobEntity) throws IOException {
        String key = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();
        s3Service.uploadFile(key, file.getInputStream());
        jobEntity.setFileName(key);
        jobService.update(jobEntity.getId(), jobEntity);
    }

    @Override
    public FileInformation getUploadFileUrl(String jobId) {
        Optional<JobEntity> optionalJobEntity = jobService.getJobEntity(jobId);
        if (optionalJobEntity.isEmpty()) {
            throw new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), String.format(THE_JOB_WITH_ID_S_DOES_NOT_EXIST, jobId));
        }
        JobEntity jobEntity = optionalJobEntity.get();
        if (!JobTypeEnum.IMPORTJOB.equals(jobEntity.getAtType())) {
            throw new UnsupportedTypeException(jobEntity.getAtType());
        }
        if (!JobStatusType.NOTSTARTED.equals(jobEntity.getStatus())) {
            throw new ProductInventoryException(HttpStatus.CONFLICT, CONFLICT.getCode(), CONFLICT.getStatus(), String.format(CANNOT_UPLOAD_FILE, jobEntity.getStatus()));
        }
        Optional<JobSpecificationEntity> jobSpecificationEntityById = jobSpecificationService.getJobSpecificationEntityById(jobEntity.getJobSpecification().getId());
        if (jobSpecificationEntityById.isEmpty()) {
            throw new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), String.format(THE_JOB_SPECIFICATION_WITH_ID_S_DOES_NOT_EXIST, jobEntity.getJobSpecification().getId()));
        }
        if (jobEntity.getFileName() == null || jobEntity.getFileName().isEmpty()) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), JOB_MISSING_FILE_NAME);
        }
        JobSpecificationEntity jobSpecificationEntity = jobSpecificationEntityById.get();
        //TO DO: add file type to s3 put request to restrict file type from job spec only
        PresignedPutObjectRequest presignedPutObjectRequest = s3Service.generatePresignedUploadUrl(jobEntity.getFileName());
        return FileInformation
                .builder()
                .url(presignedPutObjectRequest.url().toString())
                .contentType(convertFileTypeToContentType(jobSpecificationEntity.getFileType()))
                .validFor(TimePeriod
                        .builder()
                        .startDateTime(OffsetDateTime.now())
                        .endDateTime(presignedPutObjectRequest
                                .expiration()
                                .atOffset(OffsetDateTime.now().getOffset()))
                        .build())
                .build();

    }






}

