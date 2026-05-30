// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.GetJobsApiResponseDTO;
import com.orange.discobole.productinventory.dto.PageableTMF;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.job.JobEntity;
import org.springframework.util.MultiValueMap;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JobService {
    void save(JobEntity jobEntity);

    void update(String id, JobEntity jobEntity) throws ProductInventoryException;

    List<JobEntity> findJobsByStatusAndPlannedDateLessThanEqual(JobStatusType jobStatusEnum, OffsetDateTime now);

    void deleteByJobSpecificationIdIn(Collection<String> jobSpecificationIds);

    int getTotalCount(MultiValueMap<String, Object> filter);

    Optional<JobEntity> getJobEntity(String id);

    Job getJob(String id, String fields);

    ExportFileInformation getExportFileInformation(String id);

    void deleteByJobSpecificationId(String id);

    JobEntity findByJobSpecificationId(String id);

    GetJobsApiResponseDTO getJobs(
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
            List<SortJobEnum> sort
    );

    List<Job> getJobs(PageableTMF pageable);


}
