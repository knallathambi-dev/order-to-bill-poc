// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller.impl;

import com.orange.discobole.productinventory.api.v1.JobApi;
import com.orange.discobole.productinventory.dto.GetJobsApiResponseDTO;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.service.JobService;
import com.orange.discobole.productinventory.service.UploadProductsFileService;
import com.orange.discobole.productinventory.util.PageableHeader;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class JobApiImpl implements JobApi {
    private final JobService jobService;
    private final HttpServletRequest request;
    private final UploadProductsFileService uploadProductsFileService;

    @Override
    public ResponseEntity<List<Job>> listJob(
            List<String> id,
            List<String> jobSpecificationId,
            List<JobTypeEnum> atType,
            List<JobStatusType> status,
            List<OffsetDateTime> executionPeriodStartDateTime,
            OffsetDateTime executionPeriodStartDateTimeGte,
            OffsetDateTime executionPeriodStartDateTimeLte,
            List<OffsetDateTime> executionPeriodEndDateTime,
            OffsetDateTime executionPeriodEndDateTimeGte,
            OffsetDateTime executionPeriodEndDateTimeLte,
            List<OffsetDateTime> plannedDate,
            OffsetDateTime plannedDateGte,
            OffsetDateTime plannedDateLte,
            String fields,
            Integer offset,
            Integer limit,
            List<SortJobEnum> sort) {

        GetJobsApiResponseDTO jobsResponse = jobService.getJobs(id,
                jobSpecificationId,
                atType,
                status,
                executionPeriodStartDateTime,
                executionPeriodStartDateTimeGte,
                executionPeriodStartDateTimeLte,
                executionPeriodEndDateTime,
                executionPeriodEndDateTimeGte,
                executionPeriodEndDateTimeLte,
                plannedDate,
                plannedDateGte,
                plannedDateLte,
                fields,
                offset,
                limit,
                sort);
        return new ResponseEntity<>(
                jobsResponse.getJobs(),
                PageableHeader.buildPaginationHeaders(jobsResponse.getPageableTMF(), request),
                jobsResponse.isDataFitInOnPage() ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT);
    }

    @Override
    public ResponseEntity<Job> retrieveJob(String id, String fields) {
        log.info("Received request to get JobSpecification by ID");
        Job jobSpecification = jobService.getJob(id, fields);
        return ResponseEntity.ok(jobSpecification);
    }

    @Override
    public ResponseEntity<ExportFileInformation> getExportFileInformation(@PathVariable("id") String id) {
        return new ResponseEntity<>(jobService.getExportFileInformation(id), HttpStatus.OK);
    }
    @SneakyThrows
    @Override
    public ResponseEntity<FileInformation> getUploadFileUrl(String jobId) {
        return ResponseEntity.ok(uploadProductsFileService.getUploadFileUrl(jobId));
    }
}
