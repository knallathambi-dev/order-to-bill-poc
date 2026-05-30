// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.constant.Constant;
import com.orange.discobole.productinventory.constant.QueryFields;
import com.orange.discobole.productinventory.constant.Roles;
import com.orange.discobole.productinventory.dto.PageableTMF;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.exception.UnsupportedTypeException;
import com.orange.discobole.productinventory.mapper.JobSpecificationMapper;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.model.job.enumerate.FileType;
import com.orange.discobole.productinventory.util.LoggerUtil;
import com.orange.discobole.productinventory.util.PageableHeader;
import com.orange.discobole.productinventory.validation.ImportJobValidator;
import com.orange.discobole.productinventory.validation.TerminationJobValidator;
import com.orange.discobole.productinventory.validation.query.CompositeValidator;
import com.orange.discobole.productinventory.validation.query.JobSpecificationQueryValidator;
import com.orange.discobole.productinventory.validation.query.ProductQueryValidator;
import com.orange.discobole.productinventory.validation.query.QueryStringValidator;
import com.orange.discobole.productinventory.validation.query.purge.JobPurgeTypeQueryValidator;
import com.orange.discobole.productinventory.validation.query.purge.ProductPurgeTypeQueryValidator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.orange.discobole.productinventory.constant.Constant.UNAUTHORIZED_PURGE_MESSAGE;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static com.orange.discobole.productinventory.util.ApiUtil.addListToMap;
import static com.orange.discobole.productinventory.util.ApiUtil.addValueToMap;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class JobSpecificationFacade {
    private final ExportProductService exportProductService;
    private final JobSpecificationService jobSpecificationService;
    private final JobSchedulerService jobSchedulerService;
    private final JobSpecificationMapper mapper;
    private final SecurityService securityService;
    private final HrefGeneratorService hrefGeneratorService;


    public JobSpecification createAndScheduleJob(JobSpecification jobSpecification) throws UnsupportedTypeException {
        validateRequestBodyParameters(jobSpecification);
        jobSchedulerService.validateSchedule(jobSpecification.getSchedule());
        JobSpecificationEntity jobSpecificationEntity = this.createJobSpecificationEntity(jobSpecification);
        jobSchedulerService.scheduleJob(jobSpecificationEntity);
        JobSpecification dto = mapper.toDTO(jobSpecificationEntity);
        hrefGeneratorService.generateHrefJobSpecification(Collections.singletonList(dto));
        return dto;
    }

    private void validateRequestBodyParameters(JobSpecification jobSpecification) {
        if (Objects.nonNull(jobSpecification.getLifeCycleStatusChange())) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), THE_LIFE_CYCLE_STATUS_CHANGE_NOT_ADDED_IN_POST_REQUEST);
        }
        if (Objects.nonNull(jobSpecification.getActivePeriod())) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), THE_ACTIVE_PERIOD_NOT_ADDED_IN_POST_REQUEST);
        }
        if (Objects.nonNull(jobSpecification.getCreationDate())) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), THE_CREATION_DATE_NOT_ADDED_IN_POST_REQUEST);
        }
    }

    public JobSpecificationEntity createJobSpecificationEntity(JobSpecification jobSpecification) throws UnsupportedTypeException {
        if (jobSpecification instanceof ExportJobSpecification exportJob) {
            ExportJobSpecification exportJob1 = exportProductService.createExportJob(exportJob);
            JobSpecificationEntity jobSpecificationEntity = mapper.toEntity(exportJob1);
            jobSpecificationEntity.setFileType(FileType.fromValue(exportJob.getContentType()));
            return jobSpecificationService.save(jobSpecificationEntity);
        }
        if (jobSpecification instanceof PurgeJobSpecification purgeJob && isAuthorizedToPurge(purgeJob)) {
            CompositeValidator.CompositeValidatorBuilder builder = CompositeValidator.builder()
                    .addValidator(new QueryStringValidator());

            switch (purgeJob.getPurgeType()) {
                case PURGEPRODUCT -> builder.addValidator(new ProductQueryValidator())
                        .addValidator(new ProductPurgeTypeQueryValidator());
                case PURGEJOB -> builder.addValidator(new JobPurgeTypeQueryValidator())
                        .addValidator(new JobSpecificationQueryValidator());
                default -> throw new UnsupportedTypeException(purgeJob.getPurgeType());

            }

            CompositeValidator compositeValidator = builder.build();

            compositeValidator.validate(purgeJob.getQuery());
            JobSpecificationEntity jobSpecificationEntity = mapper.toEntity(purgeJob);
            return jobSpecificationService.save(jobSpecificationEntity);
        }
        if (jobSpecification instanceof TerminationJobSpecification terminationJob) {
            if (TerminationJobValidator.isCreationObjectValid(jobSpecification)) {
                JobSpecificationEntity jobSpecificationEntity = mapper.toEntity(terminationJob);
                return jobSpecificationService.save(jobSpecificationEntity);
            } else {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), TERMINATION_JOB_SUPPORTS_ONLY_IMMEDIATE_OR_ONE_TIME_JOB_SCHEDULER);
            }
        }
        if (jobSpecification instanceof ImportJobSpecification importJobSpecification) {
            if (ImportJobValidator.isCreationObjectValid(importJobSpecification)) {
                JobSpecificationEntity jobSpecificationEntity = mapper.toEntity(importJobSpecification);
                jobSpecificationEntity.setFileType(FileType.fromValue(importJobSpecification.getContentType()));
                return jobSpecificationService.save(jobSpecificationEntity);
            } else {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), IMPORT_JOB_SUPPORTS_ONLY_IMMEDIATE_OR_ONE_TIME_JOB_SCHEDULER);
            }
        }
        throw new UnsupportedTypeException(Constant.JOB_SPECIFICATION, jobSpecification.getAtType());
    }

    public Integer getTotalCount(MultiValueMap<String, Object> multiValueMap) {
        return jobSpecificationService.getTotalCount(multiValueMap);
    }


    public JobSpecification getJobSpecificationById(String id, String fields) throws ProductInventoryException {
        if (Objects.nonNull(fields) && StringUtils.isBlank(fields)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), "fields" + NOT_BE_EMPTY);
        }
        JobSpecification jobSpecificationById = jobSpecificationService.getJobSpecificationById(id, fields);
        hrefGeneratorService.generateHrefJobSpecification(Collections.singletonList(jobSpecificationById));
        return jobSpecificationById;

    }

    public void deleteJobSpecification(String id) {
        jobSpecificationService.deleteJobSpecification(id);
    }


    public JobSpecificationEntity save(JobSpecificationEntity any) {
        return this.jobSpecificationService.save(any);
    }


    private boolean isAuthorizedToPurge(PurgeJobSpecification purgeJob) {
        boolean isPurgeJobAuthorized = PurgeTypeEnum.PURGEJOB.equals(purgeJob.getPurgeType())
                && securityService.hasRoleEntitlement(Roles.PURGE_JOB);

        boolean isPurgeProductAuthorized = PurgeTypeEnum.PURGEPRODUCT.equals(purgeJob.getPurgeType())
                && securityService.hasRoleEntitlement(Roles.PURGE_PRODUCT);

        if (!(isPurgeJobAuthorized || isPurgeProductAuthorized)) {
            log.error("Unauthorized attempt to purge job: {}", LoggerUtil.sanitizeLogMessage(purgeJob.toString()));
            throw new AccessDeniedException(UNAUTHORIZED_PURGE_MESSAGE);
        }

        return true;
    }

    public List<JobSpecification> getJobSpecifications(PageableTMF pageable) {
        return jobSpecificationService.getJobSpecifications(pageable);
    }

    public ResponseEntity<List<JobSpecification>> getJobSpecifications(List<String> id, List<String> name, List<JobSpecificationStatusType> lifecycleStatus, List<JobSpecificationType> atType, List<OffsetDateTime> startDate, OffsetDateTime startDateGte, OffsetDateTime startDateLte, List<OffsetDateTime> endDate, OffsetDateTime endDateGte, OffsetDateTime endDateLte, List<OffsetDateTime> creationDate, OffsetDateTime creationDateLte, OffsetDateTime creationDateGte, String fields, Integer offset, Integer limit, List<SortJobSpecificationEnum> sort, HttpServletRequest request) {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        addListToMap(QueryFields.ID, multiValueMap, id);
        addListToMap(QueryFields.NAME, multiValueMap, name);
        addListToMap(QueryFields.LIFE_CYCLE_STATUS, multiValueMap, lifecycleStatus);
        addListToMap(QueryFields.AT_TYPE, multiValueMap, atType);
        addListToMap(QueryFields.ACTIVE_PERIOD_START_DATE_TIME, multiValueMap, startDate);
        addValueToMap(QueryFields.ACTIVE_PERIOD_START_DATE_TIME + QueryFields.GTE_SUFFIX, multiValueMap, startDateGte);
        addValueToMap(QueryFields.ACTIVE_PERIOD_START_DATE_TIME + QueryFields.LTE_SUFFIX, multiValueMap, startDateLte);
        addListToMap(QueryFields.ACTIVE_PERIOD_END_DATE_TIME, multiValueMap, endDate);
        addValueToMap(QueryFields.ACTIVE_PERIOD_END_DATE_TIME + QueryFields.GTE_SUFFIX, multiValueMap, endDateGte);
        addValueToMap(QueryFields.ACTIVE_PERIOD_END_DATE_TIME + QueryFields.LTE_SUFFIX, multiValueMap, endDateLte);
        addListToMap(QueryFields.CREATION_DATE, multiValueMap, creationDate);
        addValueToMap(QueryFields.CREATION_DATE + QueryFields.GTE_SUFFIX, multiValueMap, creationDateGte);
        addValueToMap(QueryFields.CREATION_DATE + QueryFields.LTE_SUFFIX, multiValueMap, creationDateLte);
        PageableTMF pageable = new PageableTMF(offset, limit, this.getTotalCount(multiValueMap), Objects.nonNull(sort) ? sort.stream().map(SortJobSpecificationEnum::getValue).toList() : null, fields, multiValueMap);
        List<JobSpecification> jobSpecifications = this.getJobSpecifications(pageable);
        pageable.setResultCount(jobSpecifications.size());
        hrefGeneratorService.generateHrefJobSpecification(jobSpecifications);
        HttpHeaders responseHeaders = PageableHeader.buildPaginationHeaders(pageable, request);
        boolean dataFitInOnPage = pageable.getTotalCount() <= pageable.getLimit();
        return new ResponseEntity<>(jobSpecifications, responseHeaders, dataFitInOnPage ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT);
    }
}
