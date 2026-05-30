// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.v1.JobScheduler;
import com.orange.discobole.productinventory.dto.v1.TimePeriodType;
import com.orange.discobole.productinventory.exception.UnsupportedTypeException;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationScheduler;

import java.time.*;

public interface JobSchedulerService {
    static OffsetDateTime getNextExecutionDate(JobSpecificationScheduler schedule) {
        switch (schedule.getAtType()) {
            case ONETIMEJOBSCHEDULER -> {
                return schedule.getPlannedDate();
            }
            case RECURRINGJOBSCHEDULER -> {
                return getNextRecurringExecutionDate(schedule);
            }
            case IMMEDIATEJOBSCHEDULER -> {
                return OffsetDateTime.now();
            }
            default -> throw new UnsupportedTypeException(schedule.getAtType());

        }
    }

    static OffsetDateTime getNextRecurringExecutionDate(JobSpecificationScheduler schedule) {
        LocalDate endSchedule = schedule.getScheduledPeriod().getEndDate() != null ? schedule.getScheduledPeriod().getEndDate() : null;
        int frequency = schedule.getFrequency().getAmount();
        TimePeriodType period = schedule.getFrequency().getTimePeriod();
        LocalTime executionTime = LocalTime.parse(schedule.getExecutionTime()).withNano(0);
        LocalTime now = LocalTime.now(ZoneOffset.UTC);
        LocalDate today = LocalDate.now();
        LocalDate nextDate = schedule.getScheduledPeriod().getStartDate();
        if (TimePeriodType.HOUR.equals(period)) {
            OffsetDateTime now1 = OffsetDateTime.now();
            OffsetDateTime start = mergeTimeAndDate(nextDate, executionTime);
            long hoursUntilNext = Duration.between(start, now1).toHours();
            long additionalHours = ((hoursUntilNext / frequency) + 1) * frequency;
            start = start.plusHours(additionalHours);
            while (!start.isAfter(now1)) {
                start = start.plusHours(frequency);
            }
            return start;
        }
        while (!nextDate.isAfter(today)) {
            nextDate = addPeriod(schedule.getScheduledPeriod().getStartDate(), nextDate, frequency, period);
            if (nextDate.equals(today) && executionTime.isAfter(now)) {
                return mergeTimeAndDate(nextDate, executionTime);
            }
            if (endSchedule != null && nextDate.isAfter(endSchedule)) {
                return null; // No more valid dates
            }
        }
        return mergeTimeAndDate(nextDate, executionTime);
    }

    private static OffsetDateTime mergeTimeAndDate(LocalDate nextDate, LocalTime executionTime) {
        return OffsetDateTime.of(nextDate, executionTime, ZoneOffset.UTC);
    }


    static LocalDate addPeriod(LocalDate originalDate, LocalDate date, int frequency, TimePeriodType period) {
        return switch (period) {
            case HOUR -> date;
            case DAY -> date.plusDays(frequency);
            case WEEK -> date.plusWeeks(frequency);
            case MONTH -> {
                if (originalDate.getDayOfMonth() > date.lengthOfMonth()) {
                    date = date.withDayOfMonth(date.lengthOfMonth());
                } else {
                    date = date.withDayOfMonth(originalDate.getDayOfMonth());
                }

                yield date.plusMonths(frequency);
            }
            case YEAR -> date.plusYears(frequency);
        };
    }

    boolean scheduleJob(JobSpecificationEntity jobSpecification);
    void validateSchedule(JobScheduler schedule);
}
