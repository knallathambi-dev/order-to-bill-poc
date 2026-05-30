// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller.impl;

import com.orange.discobole.productinventory.api.v1.JobSpecificationApi;
import com.orange.discobole.productinventory.dto.GetJobsApiResponseDTO;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.service.JobService;
import com.orange.discobole.productinventory.service.JobSpecificationFacade;
import com.orange.discobole.productinventory.util.PageableHeader;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class JobSpecificationApiImpl implements JobSpecificationApi {
    private final HttpServletRequest request;
    private final JobSpecificationFacade jobSpecificationFacade;
    private final JobService jobService;

    @Override
    public ResponseEntity<List<JobSpecification>> listJobSpecification(
            List<String> id,
            List<String> name,
            List<JobSpecificationStatusType> lifecycleStatus,
            List<JobSpecificationType> atType,
            List<OffsetDateTime> startDate,
            OffsetDateTime startDateGte,
            OffsetDateTime startDateLte,
            List<OffsetDateTime> endDate,
            OffsetDateTime endDateGte,
            OffsetDateTime endDateLte,
            List<OffsetDateTime> creationDate,
            OffsetDateTime creationDateGte,
            OffsetDateTime creationDateLte,
            String fields,
            Integer offset,
            Integer limit,
            List<SortJobSpecificationEnum> sort
    ) {

        return jobSpecificationFacade.getJobSpecifications(id,
                name,
                lifecycleStatus,
                atType,
                startDate,
                startDateGte,
                startDateLte,
                endDate,
                endDateGte,
                endDateLte,
                creationDate,
                creationDateGte,
                creationDateLte,
                fields,
                offset,
                limit,
                sort,
                request);

    }

    @Override
    public ResponseEntity<JobSpecification> retrieveJobSpecification(String id, String fields) {
        log.info("Received request to get JobSpecification by ID");
        JobSpecification jobSpecification = jobSpecificationFacade.getJobSpecificationById(id, fields);
        return ResponseEntity.ok(jobSpecification);

    }

    @Override
    public ResponseEntity<String> deleteJobSpecification(String id) {
        jobSpecificationFacade.deleteJobSpecification(id);
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<JobSpecification> createJobSpecification(@RequestBody @Valid JobSpecification jobSpecification) {
        JobSpecification jobSaved = jobSpecificationFacade.createAndScheduleJob(jobSpecification);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION, jobSaved.getHref());
        return new ResponseEntity<>(jobSaved, responseHeaders, HttpStatus.CREATED);

    }


    @Override
    public ResponseEntity<List<Job>> listJobByjobSpecificationId(
            String jobSpecificationId,
            List<String> id,
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
            List<SortJobEnum> sort
    ) {
        GetJobsApiResponseDTO jobsResponse = jobService.getJobs(
                id,
                List.of(jobSpecificationId),
                null,
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
}
