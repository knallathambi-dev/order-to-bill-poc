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
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobReportEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntityRef;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = JobSpecificationMapper.class)
public abstract class JobMapper {
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    @Mapping(target = "atType", source = "atType", qualifiedByName = "convertAtType")
    public abstract List<Job> toDTO(List<JobEntity> jobs);

    public Job toDTO(JobEntity jobEntity) {
        Job result;
        if (Objects.isNull(jobEntity.getAtType())) {
            result = toDtoWithFullMappingJob(jobEntity);
        } else {
            result = switch (jobEntity.getAtType()) {
                case TERMINATIONJOB -> toDtoWithFullMappingTerminationJob(jobEntity);
                case EXPORTJOB -> toDtoWithFullMappingExportJob(jobEntity);
                case PURGEJOB -> toDtoWithFullMappingPurgeJob(jobEntity);
                case IMPORTJOB -> toDtoWithFullMappingImportJob(jobEntity);
            };
        }
        return result;
    }

    public Job toDtoWithReportJobWithReports(JobEntity job, JobReportEntity jobReportEntity) {
        JobWithReports result = switch (job.getAtType()) {
            case TERMINATIONJOB -> toDtoWithFullMappingTerminationJob(job);
            case IMPORTJOB -> toDtoWithFullMappingImportJob(job);
            case EXPORTJOB, PURGEJOB ->
                    throw new IllegalArgumentException("Invalid @type for mapper");
        };
        result.setFailureReport(JobExecutionReport
                .builder()
                .status(ProductProcessStatusType.FAILED)
                .products(jobReportEntity
                        .getFailedProducts()
                        .stream()
                        .map(productRefEntity -> JobReportProductRef.builder()
                                .id(productRefEntity.getId().toString())
                                .name(productRefEntity.getName())
                                .atType(productRefEntity.getAtType())
                                .externalIdentifier(productRefEntity.getExternalIdentifier())
                                .failReason(productRefEntity.getFailReason())
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build()
        );
        result.setSuccessReport(JobExecutionReport
                .builder()
                .status(ProductProcessStatusType.SUCCEEDED)
                .products(jobReportEntity
                        .getSucceededProducts()
                        .stream()
                        .map(productRefEntity -> JobReportProductRef.builder()
                                .id(productRefEntity.getId().toString())
                                .name(productRefEntity.getName())
                                .atType(productRefEntity.getAtType())
                                .externalIdentifier(productRefEntity.getExternalIdentifier())
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build()
        );

        return result;
    }

    @Named("convertAtType")
    public String convertAtType(JobSpecificationType jobSpecificationType) {
        return jobSpecificationType.getValue();
    }

    @Named("jobSpecificationRefMapping")
    public JobSpecificationRef jobSpecificationRefMapping(JobSpecificationEntityRef jobSpecificationEntityRef) {
        return JobSpecificationRef
                .builder()
                .id(jobSpecificationEntityRef.getId())
                .build();
    }

    @Named("toDtoWithFullMappingJob")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract Job toDtoWithFullMappingJob(JobEntity jobSpecification);

    @Named("toDtoWithFullMappingTerminationJob")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract TerminationJob toDtoWithFullMappingTerminationJob(JobEntity jobEntity);

    @Named("toDtoWithFullMappingExportJob")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract ExportJob toDtoWithFullMappingExportJob(JobEntity jobEntity);

    @Named("toDtoWithFullMappingPurgeJob")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract PurgeJob toDtoWithFullMappingPurgeJob(JobEntity jobEntity);

    @Named("toDtoWithFullMappingImportJob")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract ImportJob toDtoWithFullMappingImportJob(JobEntity jobEntity);
}
