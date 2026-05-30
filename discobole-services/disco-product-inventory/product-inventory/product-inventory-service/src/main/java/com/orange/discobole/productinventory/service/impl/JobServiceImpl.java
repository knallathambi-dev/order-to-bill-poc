// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.constant.QueryFields;
import com.orange.discobole.productinventory.dto.GetJobsApiResponseDTO;
import com.orange.discobole.productinventory.dto.PageableTMF;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.mapper.JobMapper;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobReportEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.repository.JobReportRepository;
import com.orange.discobole.productinventory.repository.JobRepository;
import com.orange.discobole.productinventory.repository.JobSpecificationRepository;
import com.orange.discobole.productinventory.service.ExportProductService;
import com.orange.discobole.productinventory.service.FilterQueryService;
import com.orange.discobole.productinventory.service.JobService;
import com.orange.discobole.productinventory.validation.pageable.impl.FieldFetcher;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.OffsetDateTime;
import java.util.*;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.RESOURCE_NOT_FOUND;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.THE_JOB_SPECIFICATION_WITH_ID_RELATED_TO_JOB_WITH_ID_DOES_NOT_EXIST;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.THE_JOB_WITH_ID_S_DOES_NOT_EXIST;
import static com.orange.discobole.productinventory.util.ApiUtil.addListToMap;
import static com.orange.discobole.productinventory.util.ApiUtil.addValueToMap;

@Service
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@Slf4j
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final JobSpecificationRepository jobSpecificationRepository;
    private final FilterQueryService filterQueryService;
    private final JobReportRepository jobReportRepository;
    private final JobMapper mapper;
    private final ExportProductService exportProductService;
    private final MongoTemplate mongoTemplate;

    private final String[] requiredFields = new String[]{Job.Fields.id, Job.Fields.atType, Job.Fields.jobSpecification};
    private final HrefGeneratorServiceImpl hrefGeneratorService;

    @Override
    public void save(JobEntity jobEntity) {
        jobRepository.save(jobEntity);
    }

    @Override
    public void update(String id, JobEntity jobEntity) throws ProductInventoryException {
        if (jobRepository.existsById(id)) {
            jobEntity.setId(id);
            jobRepository.save(jobEntity);
        }
    }

    @Override
    public List<JobEntity> findJobsByStatusAndPlannedDateLessThanEqual(JobStatusType jobStatusEnum, OffsetDateTime now) {
        return jobRepository.findJobsByStatusAndPlannedDateLessThanEqual(jobStatusEnum, now);
    }

    @Override
    public void deleteByJobSpecificationIdIn(Collection<String> jobSpecificationIds) {
        mongoTemplate.remove(
                Query.query(Criteria.where("jobSpecification.id").in(jobSpecificationIds)),
                JobEntity.class
        );
    }

    @Override
    public int getTotalCount(MultiValueMap<String, Object> filter) {
        Query query = filterQueryService.createQuery(filter);
        return (int) mongoTemplate.count(query, JobEntity.class);
    }

    @Override
    public Optional<JobEntity> getJobEntity(String id) {
        return jobRepository.findById(id);
    }
    @Override
    public Job getJob(String id, String fields) {
        Query query = new Query();
        query.addCriteria(Criteria.where(JobSpecificationEntity.Fields.id).is(id));
        String[] fieldArray = filterQueryService.extractFields(fields, requiredFields);
        query.fields().include(fieldArray);
        JobEntity jobEntity = mongoTemplate.findOne(query, JobEntity.class);
        if (jobEntity == null) {
            throw new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), String.format(THE_JOB_WITH_ID_S_DOES_NOT_EXIST, id));
        }
        if (jobEntity.getAtType().equals(JobTypeEnum.TERMINATIONJOB) || jobEntity.getAtType().equals(JobTypeEnum.IMPORTJOB)) {
            Optional<JobReportEntity> byJobId = jobReportRepository.findByJobId(jobEntity.getId());
            Job job = byJobId.isPresent()
                    ? mapper.toDtoWithReportJobWithReports(jobEntity, byJobId.get())
                    : mapper.toDTO(jobEntity);
            hrefGeneratorService.generateHrefJob(Collections.singletonList(job));
            return job;
        } else {
            Job job = mapper.toDTO(jobEntity);
            hrefGeneratorService.generateHrefJob(Collections.singletonList(job));
            return job;
        }

    }

    @Override
    public ExportFileInformation getExportFileInformation(String id) throws ProductInventoryException {
        JobEntity jobEntity = jobRepository
                .findById(id)
                .orElseThrow(() -> new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), String.format(THE_JOB_WITH_ID_S_DOES_NOT_EXIST, id)));
        JobSpecificationEntity jobSpecificationEntity = jobSpecificationRepository.findById(jobEntity.getJobSpecification().getId())
                .orElseThrow(() -> new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(),
                        String.format(THE_JOB_SPECIFICATION_WITH_ID_RELATED_TO_JOB_WITH_ID_DOES_NOT_EXIST, jobEntity.getJobSpecification().getId(), jobEntity.getId())));

       return exportProductService.getExportFileInformation(jobEntity, jobSpecificationEntity);
    }

    @Override
    public void deleteByJobSpecificationId(String id) {
        jobRepository.deleteByJobSpecificationId(id);
    }

    @Override
    public JobEntity findByJobSpecificationId(String id) {
        return jobRepository.findByJobSpecification_Id(id);
    }

    @Override
    public GetJobsApiResponseDTO getJobs(
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
    ) {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        addListToMap(QueryFields.ID, multiValueMap, id);
        addListToMap(QueryFields.STATUS, multiValueMap, status);
        addListToMap(QueryFields.AT_TYPE, multiValueMap, atType);
        addListToMap(QueryFields.EXECUTION_PERIOD_START_DATE, multiValueMap, executionPeriodStartDateTime);
        addListToMap(QueryFields.JOB_SPECIFICATION_ID, multiValueMap, jobSpecificationId);
        addValueToMap(QueryFields.EXECUTION_PERIOD_START_DATE + QueryFields.GTE_SUFFIX, multiValueMap, executionPeriodStartDateTimeGte);
        addValueToMap(QueryFields.EXECUTION_PERIOD_START_DATE + QueryFields.LTE_SUFFIX, multiValueMap, executionPeriodStartDateTimeLte);
        addListToMap(QueryFields.EXECUTION_PERIOD_END_DATE, multiValueMap, executionPeriodEndDateTime);
        addValueToMap(QueryFields.EXECUTION_PERIOD_END_DATE + QueryFields.GTE_SUFFIX, multiValueMap, executionPeriodEndDateTimeGte);
        addValueToMap(QueryFields.EXECUTION_PERIOD_END_DATE + QueryFields.LTE_SUFFIX, multiValueMap, executionPeriodEndDateTimeLte);
        addListToMap(QueryFields.PLANNED_DATE, multiValueMap, plannedDate);
        addValueToMap(QueryFields.PLANNED_DATE + QueryFields.GTE_SUFFIX, multiValueMap, plannedDateGte);
        addValueToMap(QueryFields.PLANNED_DATE + QueryFields.LTE_SUFFIX, multiValueMap, plannedDateLte);
        PageableTMF pageable = new PageableTMF(offset, limit, this.getTotalCount(multiValueMap), Objects.nonNull(sort) ? sort.stream().map(SortJobEnum::getValue).toList() : null, fields, multiValueMap);
        List<Job> jobs = this.getJobs(pageable);
        hrefGeneratorService.generateHrefJob(jobs);
        pageable.setResultCount(jobs.size());
        boolean dataFitInOnPage = pageable.getTotalCount() <= pageable.getLimit();
        return new GetJobsApiResponseDTO(jobs, pageable, dataFitInOnPage);
    }

    @Override
    public List<Job> getJobs(PageableTMF pageable) {
        Query query = filterQueryService.createAndValidateQuery(pageable, new FieldFetcher<>(Job.class), requiredFields);
        return mapper.toDTO(mongoTemplate.find(query, JobEntity.class));
    }

}
