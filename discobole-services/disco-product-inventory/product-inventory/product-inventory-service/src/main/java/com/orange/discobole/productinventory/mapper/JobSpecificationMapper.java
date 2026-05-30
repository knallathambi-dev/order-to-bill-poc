// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.mapper;

import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntityRef;
import com.orange.discobole.productinventory.model.job.JobSpecificationScheduler;
import com.orange.discobole.productinventory.model.job.enumerate.FileType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;

import java.util.*;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public abstract class JobSpecificationMapper {


    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    @Mapping(target = "atType", source = "atType", qualifiedByName = "convertAtType")
    public JobSpecification toDTO(JobSpecificationEntity jobSpecification) {
        JobSpecification result;
        if (Objects.isNull(jobSpecification.getAtType())) {
            result = toDtoWithFullMappingJob(jobSpecification);
        } else {
            result = switch (jobSpecification.getAtType()) {
                case TERMINATIONJOBSPECIFICATION -> toDtoWithFullMappingTerminationJob(jobSpecification);
                case EXPORTJOBSPECIFICATION -> toDtoWithFullMappingExportJob(jobSpecification);
                case PURGEJOBSPECIFICATION -> toDtoWithFullMappingPurgeJob(jobSpecification);
                case IMPORTJOBSPECIFICATION -> toDtoWithFullMappingImportJob(jobSpecification);
            };
        }
        return result;
    }

    @Named("jobEntityRefToDTO")
    public JobSpecification toDTO(JobSpecificationEntityRef jobSpecification) {
        return JobSpecification.builder().id(jobSpecification.getId()).build();
    }


    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    protected abstract TerminationJobSpecification toDtoWithFullMappingTerminationJob(JobSpecificationEntity jobSpecification);

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    @Mapping(target = "contentType", source = "fileType", qualifiedByName = "convertFileType")
    @Mapping(target = "fields", source = "fields", qualifiedByName = "convertFields")
    protected abstract ExportJobSpecification toDtoWithFullMappingExportJob(JobSpecificationEntity jobSpecification);
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    protected abstract PurgeJobSpecification toDtoWithFullMappingPurgeJob(JobSpecificationEntity jobSpecification);
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    @Mapping(target = "contentType", source = "fileType", qualifiedByName = "convertFileType")
    protected abstract ImportJobSpecification toDtoWithFullMappingImportJob(JobSpecificationEntity jobSpecification);
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    public abstract JobSpecificationEntity toEntity(JobSpecification jobSpecification);

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    public abstract JobSpecificationEntity toEntity(TerminationJobSpecification jobSpecification);

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    @Mapping(target = "fileType", source = "contentType", qualifiedByName = "convertFileType")
    @Mapping(target = "fields", source = "fields", qualifiedByName = "convertFields")
    public abstract JobSpecificationEntity toEntity(ExportJobSpecification jobSpecification);

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    public abstract JobSpecificationEntity toEntity(PurgeJobSpecification jobSpecification);

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    public abstract JobSpecificationEntity toEntity(ImportJobSpecification jobSpecification);
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    protected abstract JobSpecification toDtoWithFullMappingJob(JobSpecificationEntity jobSpecification);

    public String convertAtType(JobSpecificationType jobTypeEnum) {
        return jobTypeEnum.getValue();
    }
    @Named("convertFileType")
    public FileType convertFileType(String fileType) {
        return FileType.fromValue(fileType);
    }

    @Named("convertFileType")
    public FileType convertFileType(ContentTypeEnum fileType) {
        return FileType.fromValue(fileType);
    }

    @Named("convertFileType")
    public ContentTypeEnum convertFileType(FileType fileType) {
        return convertFileTypeToContentType(fileType);
    }

    public static ContentTypeEnum convertFileTypeToContentType(FileType fileType) {
        return fileType != null ? switch (fileType) {
            case JSON -> ContentTypeEnum.JSON;
            case CSV -> ContentTypeEnum.CSV;
        } : null;
    }

    public JobSpecificationType map(String value) {
        return value != null ? JobSpecificationType.fromValue(value) : null;
    }

    protected JobScheduler jobSpecificationSchedulerToJobScheduler(JobSpecificationScheduler jobSpecificationScheduler) {
        if (jobSpecificationScheduler == null) {
            return null;
        }

        return switch (jobSpecificationScheduler.getAtType()) {
            case IMMEDIATEJOBSCHEDULER -> ImmediateJobScheduler
                    .builder()
                    .build();
            case ONETIMEJOBSCHEDULER -> OneTimeJobScheduler
                    .builder()
                    .plannedDate(jobSpecificationScheduler.getPlannedDate())
                    .build();
            case RECURRINGJOBSCHEDULER -> RecurringJobScheduler
                    .builder()
                    .frequency(jobSpecificationScheduler.getFrequency())
                    .scheduledPeriod(jobSpecificationScheduler.getScheduledPeriod())
                    .executionTime(jobSpecificationScheduler.getExecutionTime())
                    .build();
        };
    }

    public JobSpecificationScheduler jobScheduleToJobScheduleEntity(JobScheduler jobSchedule) {
        if (jobSchedule == null) {
            return null;
        }
        JobSpecificationScheduler.JobSpecificationSchedulerBuilder jobSpecificationSchedulerBuilder = JobSpecificationScheduler.builder();

        if (jobSchedule.getAtType() != null) {
            jobSpecificationSchedulerBuilder.atType(
                    switch (jobSchedule.getAtType()) {
                        case "OneTimeJobScheduler" -> JobSchedulerType.ONETIMEJOBSCHEDULER;
                        case "RecurringJobScheduler" -> JobSchedulerType.RECURRINGJOBSCHEDULER;
                        case "ImmediateJobScheduler" -> JobSchedulerType.IMMEDIATEJOBSCHEDULER;
                        default ->
                                throw new IllegalArgumentException("Unexpected value '" + jobSchedule.getAtType() + "'");
                    }
            );
        }

        if (jobSchedule instanceof OneTimeJobScheduler oneTimeJobScheduler) {
            jobSpecificationSchedulerBuilder.plannedDate(oneTimeJobScheduler.getPlannedDate());
        }
        if (jobSchedule instanceof RecurringJobScheduler recurringJobSchedule) {
            jobSpecificationSchedulerBuilder.frequency(recurringJobSchedule.getFrequency());
            jobSpecificationSchedulerBuilder.scheduledPeriod(recurringJobSchedule.getScheduledPeriod());
            jobSpecificationSchedulerBuilder.executionTime(recurringJobSchedule.getExecutionTime());
        }

        return jobSpecificationSchedulerBuilder.build();
    }

    @Named("convertFields")
    protected List<String> convertFields(Set<String> fields) {
        if (fields == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(fields);
    }

    @Named("convertFields")
    protected Set<String> convertFields(List<String> fields) {
        if (fields == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(fields);
    }
}
