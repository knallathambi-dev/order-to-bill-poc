// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;

import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntityRef;
import com.orange.discobole.productinventory.model.job.JobSpecificationScheduler;
import com.orange.discobole.productinventory.service.JobSchedulerService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ChangeUnit(id = "create-termination-job-specification", order = "006", author = "khames")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class CreateTerminationJobSpecification {
    public static final String TERMINATION_JOB_NAME = "Termination Job";
    private final MongoTemplate mongoTemplate;

    public static JobSpecificationEntity getTerminationJob() {
        List<JobSpecificationStatusChange> jobSpecificationStatusChanges = List.of(
                JobSpecificationStatusChange
                        .builder()
                        .lifecycleStatus(JobSpecificationStatusType.ACTIVE)
                        .changeDate(OffsetDateTime
                                .now()).build()
        );
        return JobSpecificationEntity.builder()
                .name(TERMINATION_JOB_NAME)
                .activePeriod(TimePeriod
                        .builder()
                        .startDateTime(OffsetDateTime
                                .now())
                        .endDateTime(OffsetDateTime
                                .now()
                                .plusYears(50)
                        ).build())
                .lifecycleStatus(JobSpecificationStatusType.ACTIVE)
                .creationDate(OffsetDateTime.now())
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION)
                .schedule(JobSpecificationScheduler
                        .builder()
                        .atType(JobSchedulerType.RECURRINGJOBSCHEDULER)
                        .frequency(JobFrequency
                                .builder()
                                .amount(1)
                                .timePeriod(TimePeriodType.HOUR)
                                .build()
                        )
                        .scheduledPeriod(DatePeriod.builder()
                                .startDate(LocalDate
                                        .now())
                                .endDate(LocalDate.now().plusYears(50))
                                .build())
                        .executionTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .build()
                )
                .lifeCycleStatusChange(jobSpecificationStatusChanges)
                .build();
    }

    public static JobEntity getJob(JobSpecificationEntity save) {
        return JobEntity
                .builder()
                .plannedDate(JobSchedulerService.getNextRecurringExecutionDate(save.getSchedule()))
                .jobSpecification(JobSpecificationEntityRef.builder().id(save.getId()).build())
                .atType(JobTypeEnum.TERMINATIONJOB)
                .status(JobStatusType.NOTSTARTED)
                .build();
    }

    @Execution
    public void execute() {
        JobSpecificationEntity terminationJob = getTerminationJob();
        JobSpecificationEntity save = mongoTemplate.save(terminationJob);
        JobEntity job = getJob(save);
        mongoTemplate.save(job);
    }

    @RollbackExecution
    public void rollback() {
        log.debug("rollback method");
    }
}
