// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.dto.PageableTMF;
import com.orange.discobole.productinventory.dto.v1.JobSpecification;
import com.orange.discobole.productinventory.dto.v1.JobSpecificationStatusChange;
import com.orange.discobole.productinventory.dto.v1.JobSpecificationStatusType;
import com.orange.discobole.productinventory.dto.v1.TimePeriod;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.mapper.JobSpecificationMapper;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.repository.JobReportRepository;
import com.orange.discobole.productinventory.repository.JobSpecificationRepository;
import com.orange.discobole.productinventory.service.FilterQueryService;
import com.orange.discobole.productinventory.service.JobService;
import com.orange.discobole.productinventory.service.JobSpecificationService;
import com.orange.discobole.productinventory.service.S3Service;
import com.orange.discobole.productinventory.validation.pageable.impl.FieldFetcher;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.*;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class JobSpecificationServiceImpl implements JobSpecificationService {

    private final JobSpecificationRepository jobSpecificationRepository;
    private final JobService jobService;
    private final FilterQueryService filterQueryService;
    private final MongoTemplate mongoTemplate;
    private final TransactionTemplate transactionTemplate;
    private final JobReportRepository jobReportRepository;
    private final String[] requiredFields = new String[]{JobSpecification.Fields.id, JobSpecification.Fields.atType, JobSpecification.Fields.creationDate};
    private final JobSpecificationMapper mapper;
    private final S3Service s3Service;
    @Override
    public JobSpecification getJobSpecificationById(String id, String fields) throws ProductInventoryException {
        Query query = new Query();
        query.addCriteria(Criteria.where(JobSpecificationEntity.Fields.id).is(id));
        String[] fieldArray = filterQueryService.extractFields(fields, requiredFields);
        query.fields().include(fieldArray);
        JobSpecificationEntity jobSpecification = mongoTemplate.findOne(query, JobSpecificationEntity.class);
        if (jobSpecification == null) {
            log.error("The jobSpecification with id {} does not exist", id);
            throw new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), String.format(THE_JOB_SPECIFICATION_WITH_ID_S_DOES_NOT_EXIST, id));
        }
        return mapper.toDTO(jobSpecification);
    }
    @Override
    public Optional<JobSpecificationEntity> getJobSpecificationEntityById(String id) throws ProductInventoryException {
        return jobSpecificationRepository.findById(id);
    }

    @Override
    public List<JobSpecification> getJobSpecifications(PageableTMF pageable) {
        log.debug("Getting jobs with attributes: {}", pageable);
        List<JobSpecificationEntity> jobEntities = mongoTemplate.find(filterQueryService.createAndValidateQuery(pageable, new FieldFetcher<>(JobSpecification.class), requiredFields), JobSpecificationEntity.class);
        return jobEntities.stream().map(mapper::toDTO).toList();
    }

    @Override
    public JobSpecificationEntity save(JobSpecificationEntity jobSpecificationEntity) {
        jobSpecificationEntity.setCreationDate(OffsetDateTime.now());
        jobSpecificationEntity.setLifecycleStatus(JobSpecificationStatusType.CREATED);
        updateStatusChange(jobSpecificationEntity);
        return jobSpecificationRepository.save(jobSpecificationEntity);
    }

    @Override
    public JobSpecificationEntity updateJobSpecificationEntity(JobSpecificationEntity jobSpecificationEntity) {
        if (jobSpecificationEntity.getActivePeriod() == null) {
            jobSpecificationEntity.setActivePeriod(TimePeriod.builder().build());
        }
        if (jobSpecificationEntity.getActivePeriod().getStartDateTime() == null) {
            jobSpecificationEntity.getActivePeriod().setStartDateTime(OffsetDateTime.now());
        }
        updateStatusChange(jobSpecificationEntity);
        return jobSpecificationRepository.save(jobSpecificationEntity);
    }

    private void updateStatusChange(JobSpecificationEntity jobSpecificationEntity) {
        List<JobSpecificationStatusChange> lifeCycleStatusChange = jobSpecificationEntity.getLifeCycleStatusChange();

        if (lifeCycleStatusChange == null) {
            lifeCycleStatusChange = new ArrayList<>();
        }

        if (!lifeCycleStatusChange.isEmpty()) {
            JobSpecificationStatusChange latestChange = lifeCycleStatusChange
                    .stream()
                    .max(Comparator.comparing(JobSpecificationStatusChange::getChangeDate))
                    .orElseThrow();

            if (!latestChange.getLifecycleStatus().equals(jobSpecificationEntity.getLifecycleStatus())) {
                lifeCycleStatusChange.add(createStatusChange(jobSpecificationEntity));
            }
        } else {
            lifeCycleStatusChange.add(createStatusChange(jobSpecificationEntity));
        }

        jobSpecificationEntity.setLifeCycleStatusChange(lifeCycleStatusChange);
    }

    private JobSpecificationStatusChange createStatusChange(JobSpecificationEntity jobSpecificationEntity) {
        JobSpecificationStatusChange statusChange = new JobSpecificationStatusChange();
        statusChange.setChangeDate(OffsetDateTime.now());
        statusChange.setLifecycleStatus(jobSpecificationEntity.getLifecycleStatus());
        return statusChange;
    }

    @Override
    public Integer getTotalCount(MultiValueMap<String, Object> multiValueMap) {
        Query query = filterQueryService.createQuery(multiValueMap);
        return (int) mongoTemplate.count(query, JobSpecificationEntity.class);
    }

    @Override
    public void deleteJobSpecification(String id) {
        Optional<JobSpecificationEntity> byId = jobSpecificationRepository.findById(id);
        Optional<JobEntity> jobEntity = Optional.ofNullable(jobService.findByJobSpecificationId(id));

        if (byId.isEmpty()) {
            throw new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), String.format(JOB_SPECIFICATION_NOT_FOUND, id));
        }
        JobSpecificationEntity jobSpecificationEntity = byId.get();
        // @formatter:off
            if (JobSpecificationStatusType.CREATED.equals(jobSpecificationEntity.getLifecycleStatus()) ||
                    JobSpecificationStatusType.TERMINATED.equals(jobSpecificationEntity.getLifecycleStatus())) {
            // @formatter:on
                transactionTemplate.execute(status -> {
                    mongoTemplate.remove(jobSpecificationEntity);
                    jobService.deleteByJobSpecificationId(id);
                    jobReportRepository.deleteByJobSpecificationId(id);

                    if (!jobEntity.isEmpty()) {
                        String fileName = jobEntity.get().getFileName();
                        try {
                            s3Service.deleteFiles(Collections.singletonList(fileName));
                        } catch (IOException e) {
                            throw new ProductInventoryException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR.getCode(), INTERNAL_ERROR.getStatus(),  "Failed to process export job: " + e.getMessage());
                        }
                    }
                    return null;
                });
            } else {
                throw new ProductInventoryException(HttpStatus.CONFLICT, CONFLICT.getCode(), CONFLICT.getStatus(), CANNOT_DELETE_ALREADY_STARTED_JOB_SPECIFICATION);
            }
    }
}
