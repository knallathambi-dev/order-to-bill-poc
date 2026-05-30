// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.converter.JobSpecificationTypeToJobTypeEnumConverter;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.exception.FileNotReadyException;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntityRef;
import com.orange.discobole.productinventory.repository.JobSpecificationRepository;
import com.orange.discobole.productinventory.service.JobExecutorService;
import com.orange.discobole.productinventory.service.JobSchedulerService;
import com.orange.discobole.productinventory.service.JobService;
import com.orange.discobole.productinventory.service.JobSpecificationService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static com.orange.discobole.productinventory.util.FileUtils.createExportFileName;
import static com.orange.discobole.productinventory.util.FileUtils.createUploadFileName;

@Service
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@Slf4j
public class JobSchedulerServiceImpl implements JobSchedulerService {

    private final JobService jobService;
    private final JobExecutorService jobExecutorService;
    private final JobSpecificationRepository jobSpecificationRepository;
    private final JobSpecificationService jobSpecificationService;

    /**
     * Schedules a jobSpecification for execution based on its defined schedule.
     * <p>
     * This method calculates the next execution date for the specified jobSpecification using its schedule.
     * If a valid next execution date is found, the jobSpecification is saved with that planned date.
     *
     * @param jobSpecification the jobSpecification entity to be scheduled, which must contain a valid schedule.
     * @return {@code true} if the jobSpecification was successfully scheduled with a planned date;
     * {@code false} if no next execution date is available.
     */
    @Override
    public boolean scheduleJob(JobSpecificationEntity jobSpecification) {
        OffsetDateTime nextExecutionDate = JobSchedulerService.getNextExecutionDate(jobSpecification.getSchedule());
        if (nextExecutionDate != null) {
            saveScheduledJobWithPlannedDate(jobSpecification, nextExecutionDate);
            return true;
        }
        return false;
    }

    @Override
    public void validateSchedule(JobScheduler schedule) {
        if (Objects.isNull(schedule)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), JOB_SCHEDULE_MUST_NOT_BE_NULL);
        }
        if (schedule instanceof RecurringJobScheduler recurringJobSchedule) {
            if (recurringJobSchedule.getScheduledPeriod().getStartDate() == null) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(FIELD_MUST_NOT_BE_NULL, RecurringJobScheduler.Fields.scheduledPeriod + "." + DatePeriod.Fields.startDate));
            }
            if (recurringJobSchedule.getScheduledPeriod().getEndDate() != null &&
                    recurringJobSchedule.getScheduledPeriod().getStartDate().isAfter(recurringJobSchedule.getScheduledPeriod().getEndDate()
                    )
            ) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), START_SCHEDULE_CANNOT_BE_LATER_THAN_END_SCHEDULE);
            }
            if (TimePeriodType.HOUR.equals(recurringJobSchedule.getFrequency().getTimePeriod())
                    && recurringJobSchedule.getFrequency().getAmount() > 24) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), FREQUENCY_AMOUNT_CANNOT_BE_MORE_THAN_24_FOR_PERIOD_TYPE_HOUR);

            }
        }
        if (schedule instanceof OneTimeJobScheduler oneTimeJobScheduler) {
            if (oneTimeJobScheduler.getPlannedDate() == null) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(FIELD_MUST_NOT_BE_NULL, OneTimeJobScheduler.Fields.plannedDate));
            }
            if (oneTimeJobScheduler.getPlannedDate().isBefore(OffsetDateTime.now())
            ) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), PLANNED_DATE_CANNOT_BE_IN_THE_PAST);
            }
        }


    }

    private void saveScheduledJobWithPlannedDate(JobSpecificationEntity jobSpecification, OffsetDateTime now) {
        String fileName = switch (jobSpecification.getAtType()) {
            case TERMINATIONJOBSPECIFICATION, PURGEJOBSPECIFICATION -> null;
            case EXPORTJOBSPECIFICATION -> createExportFileName(jobSpecification);
            case IMPORTJOBSPECIFICATION -> createUploadFileName(jobSpecification);
        };
        JobEntity jobEntity = JobEntity
                .builder()
                .jobSpecification(JobSpecificationEntityRef
                        .builder()
                        .id(jobSpecification.getId())
                        .build()
                ).plannedDate(now)
                .status(JobStatusType.NOTSTARTED)
                .atType(JobSpecificationTypeToJobTypeEnumConverter.convert(jobSpecification.getAtType()))
                .fileName(fileName)
                .build();
        this.jobService.save(jobEntity);
    }

    public void executeJob(JobEntity job) {
        updateJobStatus(job, JobStatusType.RUNNING);
        log.info("Executing jobSpecification with id: {}", job.getId());
        JobSpecificationEntity jobSpecification = jobSpecificationRepository.findById(job.getJobSpecification().getId()).orElseThrow(() -> new NoSuchElementException(String.format("JobSpecification with id %s could not be found", job.getJobSpecification().getId())));
        try {
            updateJobSpecificationStatus(jobSpecification, JobSpecificationStatusType.ACTIVE);
            jobExecutorService.execute(jobSpecification, job);
            updateJobStatus(job, JobStatusType.SUCCEEDED);
        } catch (FileNotReadyException e) {
            log.error("Error executing job with id: {}. Error: {}", job.getId(), e.getMessage(), e);
            updateJobStatus(job, JobStatusType.NOTSTARTED);
            return;
        } catch (Exception e) {
            log.error("Error executing job with id: {}. Error: {}", job.getId(), e.getMessage(), e);
            job.setErrorLog(e.getMessage() + ": " +
                    Arrays.toString(Arrays.stream(e.getStackTrace())
                            .limit(10)
                            .toArray()));
            updateJobStatus(job, JobStatusType.FAILED);
        }
        boolean isRecurring = jobSpecification.getSchedule() != null &&
                    JobSchedulerType.RECURRINGJOBSCHEDULER.equals(jobSpecification.getSchedule().getAtType());

            boolean isReScheduled = isRecurring && this.scheduleJob(jobSpecification);
            if (!isReScheduled) {
                updateJobSpecificationStatus(jobSpecification, JobSpecificationStatusType.TERMINATED);
            }

    }

    private void updateJobStatus(JobEntity job, JobStatusType jobStatusTypeEnum) {
        job.setStatus(jobStatusTypeEnum);

        switch (jobStatusTypeEnum) {
            case NOTSTARTED:
                job.setExecutionPeriod(null);
                break;
            case RUNNING:
                job.setExecutionPeriod(
                        TimePeriod.builder()
                                .startDateTime(OffsetDateTime.now())
                                .build()
                );
                break;
            case FAILED, SUCCEEDED:
                if (job.getExecutionPeriod() != null) {
                    job.getExecutionPeriod().setEndDateTime(OffsetDateTime.now());
                }
                break;
            default:
                break;
        }

        jobService.update(job.getId(), job);
    }


    private void updateJobSpecificationStatus(JobSpecificationEntity jobSpecification, JobSpecificationStatusType jobSpecificationStatusType) {
        if (!jobSpecificationStatusType.equals(jobSpecification.getLifecycleStatus()) && jobSpecificationRepository.existsById(jobSpecification.getId())) {
            jobSpecification.setLifecycleStatus(jobSpecificationStatusType);
            jobSpecification.setActivePeriod(
                    (jobSpecification.getActivePeriod() != null && jobSpecification.getActivePeriod().getStartDateTime() != null)
                            ? jobSpecification.getActivePeriod()
                            : TimePeriod.builder().startDateTime(OffsetDateTime.now()).build());
            if (JobSpecificationStatusType.TERMINATED.equals(jobSpecificationStatusType) && jobSpecification.getActivePeriod().getEndDateTime() == null) {
                jobSpecification
                        .getActivePeriod()
                        .setEndDateTime(OffsetDateTime.now());
            }
            jobSpecificationService.updateJobSpecificationEntity(jobSpecification);
        }
    }

    @Scheduled(cron = "${scheduling.jobScheduler.cronExpression}")
    @SchedulerLock(name = "${scheduling.jobScheduler.lockName}", lockAtMostFor = "${scheduling.jobScheduler.lockAtMostFor}", lockAtLeastFor = "${scheduling.jobScheduler.lockAtLeastFor}")
    public void executeScheduledJobs() {
        List<JobEntity> jobs = jobService
                .findJobsByStatusAndPlannedDateLessThanEqual(JobStatusType.NOTSTARTED, OffsetDateTime.now());
        if (!jobs.isEmpty()) {
            log.info("Executing {} ScheduledJobs : {}", jobs.size(), jobs);
        }
        for (JobEntity job : jobs) {
            executeJob(job);
        }
    }

}
