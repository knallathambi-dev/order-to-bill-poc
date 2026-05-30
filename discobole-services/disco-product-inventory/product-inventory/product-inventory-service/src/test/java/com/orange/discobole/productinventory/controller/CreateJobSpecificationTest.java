// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.productinventory.constant.Roles;
import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.service.SecurityService;
import com.orange.discobole.productinventory.util.AbstractTest;
import com.orange.discobole.productinventory.util.creator.JobSpecificationCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION;
import static com.orange.discobole.productinventory.dto.v1.JobSpecificationStatusType.CREATED;
import static com.orange.discobole.productinventory.dto.v1.PurgeTypeEnum.PURGEPRODUCT;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.MISSING_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateJobSpecificationTest extends AbstractTest {
    private static final String EXPORT_QUERY_BY_STATUS = "status=" + ProductStatusType.CREATED.getValue();
    @MockBean
    private SecurityService securityService;

    static Stream<Arguments> jobSpecificationSchedulesAndExpectedDatesProvider() {
        // Define the date-time format
        LocalTime timeNow = OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC)
                .toLocalTime().plusMinutes(10);
        final String timeNowString = timeNow.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
     /*
    for Recurring monthly job specification, with the first execution set to last month, we need to
    Handle edge cases for dates on the 31st: When subtracting a month from a date like the 31st,
    the previous month may not have a 31st day (e.g., February). This causes the date to adjust
    to the last valid day of that month (e.g., the 28th or 30th). To maintain consistency with
    the expected behavior, we subtract one month, allow the date to adjust automatically if needed,
    and then add one month back untill we have a future date. This approach ensures the result aligns with the behavior of
    the code under test.
        */
        OffsetDateTime monthEdgeCase = OffsetDateTime.now().minusMonths(1);
        do {
            monthEdgeCase = monthEdgeCase.plusMonths(1).withNano(0);
        } while (monthEdgeCase.toLocalDate().isBefore(LocalDate.now()));

        OffsetDateTime monthEdgeCaseScheduled2MonthsAgo = OffsetDateTime.now().minusMonths(2);
        do {
            monthEdgeCaseScheduled2MonthsAgo = monthEdgeCaseScheduled2MonthsAgo.plusMonths(1).withNano(0);
        } while (monthEdgeCaseScheduled2MonthsAgo.toLocalDate().isBefore(LocalDate.now()));
        if (OffsetDateTime.now().minusMonths(2).getDayOfMonth() > monthEdgeCaseScheduled2MonthsAgo.toLocalDate().lengthOfMonth()) {
            monthEdgeCaseScheduled2MonthsAgo = monthEdgeCaseScheduled2MonthsAgo.withDayOfMonth(monthEdgeCaseScheduled2MonthsAgo.toLocalDate().lengthOfMonth());
        } else {
            monthEdgeCaseScheduled2MonthsAgo = monthEdgeCaseScheduled2MonthsAgo.withDayOfMonth(OffsetDateTime.now().minusMonths(2).getDayOfMonth());
        }

        return Stream.of(
                // Recurring daily jobSpecification, scheduled for execution tomorrow
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.DAY).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().plusDays(1))
                                                .endDate(LocalDate.now().plusWeeks(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(OffsetDateTime.now().withNano(0).plusDays(1), timeNow),
                        "Recurring daily jobSpecification, scheduled for execution tomorrow"
                ),

                // Recurring weekly jobSpecification, first execution next week
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.WEEK).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().plusWeeks(1))
                                                .endDate(LocalDate.now().plusMonths(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(OffsetDateTime.now().withNano(0).plusWeeks(1), timeNow),
                        "Recurring weekly jobSpecification, first execution next week"
                ),

                // Recurring monthly jobSpecification, first execution next month
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.MONTH).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().plusMonths(1))
                                                .endDate(LocalDate.now().plusYears(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(OffsetDateTime.now().withNano(0).plusMonths(1), timeNow),
                        "Recurring monthly jobSpecification, first execution next month"
                ),

                // Recurring yearly jobSpecification, first execution next year
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.YEAR).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().plusYears(1))
                                                .endDate(LocalDate.now().plusYears(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(OffsetDateTime.now().withNano(0).plusYears(1), timeNow),
                        "Recurring yearly jobSpecification, first execution next year"
                ),

                // Recurring daily jobSpecification, scheduled for execution yesterday
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.DAY).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().minusDays(1))
                                                .endDate(LocalDate.now().plusWeeks(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(OffsetDateTime.now().withNano(0), timeNow),
                        "Recurring daily jobSpecification, scheduled for execution yesterday"
                ),

                // Recurring weekly jobSpecification, first execution last week
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.WEEK).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().minusWeeks(1))
                                                .endDate(LocalDate.now().plusMonths(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(OffsetDateTime.now().withNano(0), timeNow),
                        "Recurring weekly jobSpecification, first execution last week"
                ),

                // Recurring monthly jobSpecification, first execution last month
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.MONTH).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().minusMonths(1))
                                                .endDate(LocalDate.now().plusYears(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(monthEdgeCase, timeNow),
                        "Recurring monthly jobSpecification, first execution last month"
                ),

                // Recurring yearly jobSpecification, first execution last year
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.YEAR).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().minusYears(1))
                                                .endDate(LocalDate.now().plusYears(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(OffsetDateTime.now().withNano(0), timeNow),
                        "Recurring yearly jobSpecification, first execution last year"
                ),

                // Recurring daily jobSpecification, scheduled for execution 2 days ago
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.DAY).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().minusDays(2))
                                                .endDate(LocalDate.now().plusWeeks(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(OffsetDateTime.now().withNano(0), timeNow),
                        "Recurring daily jobSpecification, scheduled for execution 2 days ago"
                ),

                // Recurring weekly jobSpecification, first execution 2 weeks ago
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.WEEK).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().minusWeeks(2))
                                                .endDate(LocalDate.now().plusMonths(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(OffsetDateTime.now().withNano(0), timeNow),
                        "Recurring weekly jobSpecification, first execution 2 weeks ago"
                ),

                // Recurring monthly jobSpecification, first execution 2 months ago
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.MONTH).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().minusMonths(2))
                                                .endDate(LocalDate.now().plusMonths(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(monthEdgeCaseScheduled2MonthsAgo, timeNow),
                        "Recurring monthly jobSpecification, first execution 2 months ago"
                ),

                // Recurring yearly jobSpecification, first execution 2 years ago
                Arguments.of(
                        RecurringJobScheduler.builder()
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.YEAR).build())
                                .scheduledPeriod(
                                        DatePeriod
                                                .builder()
                                                .startDate(LocalDate.now().minusYears(1))
                                                .endDate(LocalDate.now().plusYears(1))
                                                .build())
                                .executionTime(timeNowString)
                                .build(),
                        mergeTimeAndDate(OffsetDateTime.now().withNano(0), timeNow),
                        "Recurring yearly jobSpecification, first execution 2 years ago"
                )
        );
    }


    @Test
    void givenInvalidJobType_whenCreate_thenBadRequest() throws Exception {

        String invalidTypeJob = toJsonString(JobSpecification.builder().build()).replace("JobSpecification", "INVALID_TYPE");

        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(invalidTypeJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void givenNotImplementedJobType_whenCreate_thenNotImplemented() throws Exception {
        String notImplementedTypeJob = toJsonString(JobSpecification
                .builder()
                .atType("Event")
                .schedule(ImmediateJobScheduler
                        .builder()
                        .build())
                .build());
        //even if you add schedule to event you still can't create it
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(notImplementedTypeJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        resultActions.andExpect(status().isNotImplemented());
    }

    @Test
    void givenExportJobJobType_whenCreate_thenCreated() throws Exception {

        String validTypeJob = toJsonString(ExportJobSpecification.builder().atType("ExportJobSpecification").contentType(ContentTypeEnum.JSON).query(EXPORT_QUERY_BY_STATUS).schedule(ImmediateJobScheduler.builder().build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validTypeJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        ExportJobSpecification exportJob = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        Assertions.assertEquals("ExportJobSpecification", exportJob.getAtType());
    }

    @Test
    void givenOneTimeJobWithPastPlannedDate_whenCreateJob_thenBadRequest() throws Exception {
        OffsetDateTime offsetDateTime = OffsetDateTime.now().minusHours(30).withNano(0).withOffsetSameInstant(ZoneOffset.UTC);
        String jobSpecification = toJsonString(ExportJobSpecification.builder().contentType(ContentTypeEnum.JSON).query(EXPORT_QUERY_BY_STATUS).schedule(OneTimeJobScheduler.builder().plannedDate(offsetDateTime).build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), PLANNED_DATE_CANNOT_BE_IN_THE_PAST, INVALID_INPUT.getStatus());
    }

    @Test
    void givenImmediateJob_whenCreateJob_thenScheduledJobIsCreated() throws Exception {
        ExportJobSpecification exportJob = getExportJobWithImmediateType();
        assertAndGetTerminationJobCreatedFromSpec(exportJob.getId());
    }

    @Test
    void givenOneTimeJobWithCurrentPlannedDate_whenCreateJob_thenScheduledJobIsCreated() throws Exception {
        OffsetDateTime offsetDateTime = OffsetDateTime.now().plusMinutes(1).withNano(0).withOffsetSameInstant(ZoneOffset.UTC);
        ExportJobSpecification exportJob = getExportJobWithPlannedDate(offsetDateTime);
        List<JobEntity> scheduledJobEntities = assertAndGetTerminationJobCreatedFromSpec(exportJob.getId());
        Assertions.assertEquals(scheduledJobEntities.get(0).getPlannedDate(), offsetDateTime);


    }

    @Test
    void givenOneTimeJobWithFuturePlannedDate_whenCreateJob_thenScheduledJobIsCreated() throws Exception {
        OffsetDateTime offsetDateTime = OffsetDateTime.now().plusHours(30).withNano(0).withOffsetSameInstant(ZoneOffset.UTC);

        ExportJobSpecification exportJob = getExportJobWithPlannedDate(offsetDateTime);
        List<JobEntity> scheduledJobEntities = assertAndGetTerminationJobCreatedFromSpec(exportJob.getId());
        Assertions.assertEquals(scheduledJobEntities.get(0).getPlannedDate(), offsetDateTime);


    }

    private ExportJobSpecification getExportJobWithPlannedDate(OffsetDateTime offsetDateTime) throws Exception {


        String jobSpecification = toJsonString(ExportJobSpecification.builder().contentType(ContentTypeEnum.JSON).query(EXPORT_QUERY_BY_STATUS).schedule(OneTimeJobScheduler.builder().plannedDate(offsetDateTime).build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        return readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
    }

    private ExportJobSpecification getExportJobWithImmediateType() throws Exception {


        String jobSpecification = toJsonString(ExportJobSpecification.builder().contentType(ContentTypeEnum.JSON).query(EXPORT_QUERY_BY_STATUS)
                .schedule(ImmediateJobScheduler.builder().build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        return readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
    }

    @ParameterizedTest
    @MethodSource("jobSpecificationSchedulesAndExpectedDatesProvider")
    void givenDifferentSchedules_whenCreateJob_thenScheduledJobIsCreatedWithTheCorrectDate(RecurringJobScheduler jobSchedule, OffsetDateTime nextOccurrenceDate, String message) throws Exception {


        String validTypeJob = toJsonString(ExportJobSpecification.builder().contentType(ContentTypeEnum.JSON).query(
                EXPORT_QUERY_BY_STATUS).schedule(jobSchedule).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validTypeJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        ExportJobSpecification exportJob = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        List<JobEntity> scheduledJobEntities = assertAndGetTerminationJobCreatedFromSpec(exportJob.getId());
        Assertions.assertEquals(nextOccurrenceDate, scheduledJobEntities.get(0).getPlannedDate(), message);
    }

    @Test
    void givenJobWithRecurringSchedules_whenStartScheduleIsAfterEndDate_thenError() throws Exception {


        RecurringJobScheduler jobSchedule = RecurringJobScheduler.builder()
                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.DAY).build())
                .scheduledPeriod(
                        DatePeriod
                                .builder()
                                .startDate(LocalDate.now().plusDays(1))
                                .endDate(LocalDate.now().minusDays(1))
                                .build()
                )
                .executionTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .build();
        String validTypeJob = toJsonString(
                ExportJobSpecification
                        .builder()
                        .contentType(ContentTypeEnum.JSON)
                        .query(EXPORT_QUERY_BY_STATUS)
                        .schedule(jobSchedule).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validTypeJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), START_SCHEDULE_CANNOT_BE_LATER_THAN_END_SCHEDULE, INVALID_INPUT.getStatus());

    }

    @Test
    void givenNullSchedule_whenCreateJob_thenBadRequest() throws Exception {

        String validTypeJob = toJsonString(
                ExportJobSpecification
                        .builder()
                        .contentType(ContentTypeEnum.JSON)
                        .query(EXPORT_QUERY_BY_STATUS)
                        .build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validTypeJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), JOB_SCHEDULE_MUST_NOT_BE_NULL, INVALID_INPUT.getStatus());
    }

    @Test
    void givenImmediateScheduleTerminationJobWithPastPlannedDate_whenCreateTerminationJobSpecification_thenScheduledJobIsCreated() throws Exception {
        TerminationJobSpecification terminationJobSpecification = createTerminationJobSpecificationWithImmediateJobScheduler();
        assertAndGetTerminationJobCreatedFromSpec(terminationJobSpecification.getId());
    }

    @Test
    void givenOneTimeScheduleTerminationJobWithPlannedDateInTheFuture_whenCreateTerminationJobSpecification_thenScheduledJobIsCreated() throws Exception {
        OffsetDateTime plannedDate = OffsetDateTime.now().plusHours(30).withNano(0).withOffsetSameInstant(ZoneOffset.UTC);
        TerminationJobSpecification terminationJobSpecification = createTerminationJobWithOneTimeJobSchedulerSpecification(plannedDate);
        List<JobEntity> scheduledJobEntities = assertAndGetTerminationJobCreatedFromSpec(terminationJobSpecification.getId());
        Assertions.assertEquals(scheduledJobEntities.get(0).getPlannedDate(), plannedDate);
    }


    private TerminationJobSpecification createTerminationJobWithOneTimeJobSchedulerSpecification(OffsetDateTime plannedDate) throws Exception {
        String jobSpecification = toJsonString(TerminationJobSpecification.builder().atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION.getValue()).schedule(
                OneTimeJobScheduler
                        .builder()
                        .atType(JobSchedulerType.ONETIMEJOBSCHEDULER.getValue())
                        .plannedDate(plannedDate)
                        .build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        return readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
    }

    @Test
    void givenRecurringScheduleTerminationJob_whenCreateTerminationJobSpecification_thenExceptionThrown() throws Exception {
        ResultActions resultActions = createTerminationJobWithRecurringJobSchedulerSpecification();
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), TERMINATION_JOB_SUPPORTS_ONLY_IMMEDIATE_OR_ONE_TIME_JOB_SCHEDULER, INVALID_INPUT.getStatus());
    }

    private ResultActions createTerminationJobWithRecurringJobSchedulerSpecification() {
        String jobSpecification = toJsonString(TerminationJobSpecification
                .builder()
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION.getValue())
                .schedule(RecurringJobScheduler
                        .builder()
                        .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.MONTH).build())
                        .scheduledPeriod(DatePeriod.builder()
                                .startDate(LocalDate.now().plusMonths(1))
                                .endDate(LocalDate.now().plusYears(1))
                                .build())
                        .executionTime("14:00:00")
                        .build())
                .build());
        return callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
    }
    @Test
    void givenTerminationJobSpecificationWithNullSchedule_whenCreateTerminationJobSpecification_thenCreatedWithDefaultScheduler() throws Exception {
        TerminationJobSpecification terminationJobSpecification = createTerminationJobSpecificationWithImmediateJobScheduler();
        JobSpecificationEntity jobSpecificationEntity = mongoTemplate.findById(terminationJobSpecification.getId(), JobSpecificationEntity.class);
        Assertions.assertNotNull(jobSpecificationEntity.getSchedule());
        Assertions.assertEquals(JobSchedulerType.IMMEDIATEJOBSCHEDULER, jobSpecificationEntity.getSchedule().getAtType());
    }



    @Test
    void createJobSpecificationWithCreationDate_thenExceptionThrown() throws Exception {
        String jobSpecification = toJsonString(TerminationJobSpecification.builder().atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION.getValue()).creationDate(OffsetDateTime.parse("2026-01-15T09:30:29Z")).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), THE_CREATION_DATE_NOT_ADDED_IN_POST_REQUEST, INVALID_INPUT.getStatus());
    }

    @Test
    void createJobSpecificationWithLifeCycleStatusChange_thenExceptionThrown() throws Exception {
        String jobSpecification = toJsonString(TerminationJobSpecification.builder()
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION.getValue())
                .lifeCycleStatusChange(List.of(
                        JobSpecificationStatusChange.builder()
                                .changeDate(OffsetDateTime.parse("2026-01-15T09:30:29Z"))
                                .lifecycleStatus(CREATED).build())).build());

        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), THE_LIFE_CYCLE_STATUS_CHANGE_NOT_ADDED_IN_POST_REQUEST, INVALID_INPUT.getStatus());
    }

    @Test
    void createJobSpecificationWithActivePeriod_thenExceptionThrown() throws Exception {
        String jobSpecification = toJsonString(TerminationJobSpecification.builder()
                .atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION.getValue())
                .activePeriod(TimePeriod.builder()
                        .startDateTime(OffsetDateTime.parse("2028-01-14T03:52:24Z"))
                        .endDateTime(OffsetDateTime.parse("2028-01-14T03:52:26Z"))
                        .build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), THE_ACTIVE_PERIOD_NOT_ADDED_IN_POST_REQUEST, INVALID_INPUT.getStatus());
    }


    @Test
    void givenImmediateScheduleImportJobWithPastPlannedDate_whenCreateImportJobSpecification_thenScheduledTaskIsCreated() throws Exception {
        ImportJobSpecification importJobSpecification = createImportJobSpecificationWithImmediateJobScheduler();
        List<JobEntity> scheduledTaskEntities = assertAndGetTerminationJobCreatedFromSpec(importJobSpecification.getId());
        Assertions.assertEquals(JobTypeEnum.IMPORTJOB, scheduledTaskEntities.get(0).getAtType());
    }

    @Test
    void givenOneTimeScheduleImportJobWithPlannedDateInTheFuture_whenCreateImportJobSpecification_thenScheduledTaskIsCreated() throws Exception {
        OffsetDateTime plannedDate = OffsetDateTime.now().plusHours(30).withNano(0).withOffsetSameInstant(ZoneOffset.UTC);
        ImportJobSpecification importJobSpecification = createImportJobWithOneTimeJobSchedulerSpecification(plannedDate);
        List<JobEntity> scheduledTaskEntities = assertAndGetTerminationJobCreatedFromSpec(importJobSpecification.getId());
        Assertions.assertEquals(scheduledTaskEntities.get(0).getPlannedDate(), plannedDate);
        Assertions.assertEquals(JobTypeEnum.IMPORTJOB, scheduledTaskEntities.get(0).getAtType());
    }


    private ImportJobSpecification createImportJobWithOneTimeJobSchedulerSpecification(OffsetDateTime plannedDate) throws Exception {
        String jobSpecification = toJsonString(JobSpecificationCreator
                .createImportJobSpecificationBuilderWithOneTimeJobScheduler(plannedDate).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        return readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
    }

    private ImportJobSpecification createImportJobSpecificationWithImmediateJobScheduler() throws Exception {
        String jobSpecification = toJsonString(ImportJobSpecification.builder().atType(JobSpecificationType.IMPORTJOBSPECIFICATION.getValue()).schedule(
                        ImmediateJobScheduler
                                .builder()
                                .atType(JobSchedulerType.IMMEDIATEJOBSCHEDULER.getValue())
                                .build())
                .importType(ImportTypeEnum.INSERT).contentType(ContentTypeEnum.JSON)
                .build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        return readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
    }

    @Test
    void givenRecurringScheduleImportJob_whenCreateImportJobSpecification_thenExceptionThrown() throws Exception {
        ResultActions resultActions = createImportJobWithRecurringJobSchedulerSpecification();
        resultActions.andExpect(status().isBadRequest());
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), IMPORT_JOB_SUPPORTS_ONLY_IMMEDIATE_OR_ONE_TIME_JOB_SCHEDULER, INVALID_INPUT.getStatus());
    }

    private ResultActions createImportJobWithRecurringJobSchedulerSpecification() {
        String jobSpecification = toJsonString(ImportJobSpecification
                .builder()
                .atType(JobSpecificationType.IMPORTJOBSPECIFICATION.getValue())
                .importType(ImportTypeEnum.INSERT)
                .schedule(RecurringJobScheduler
                        .builder()
                        .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.MONTH).build())
                        .scheduledPeriod(DatePeriod.builder()
                                .startDate(LocalDate.now().plusMonths(1))
                                .endDate(LocalDate.now().plusYears(1))
                                .build())
                        .executionTime("14:00:00")
                        .build())
                .build());
        return callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
    }
    @Test
    void givenImportJobSpecificationWithNullSchedule_whenCreateImportJobSpecification_thenCreatedWithDefaultScheduler() throws Exception {
        ImportJobSpecification importJobSpecification = createImportJobSpecificationWithImmediateJobScheduler();
        JobSpecificationEntity jobSpecificationEntity = mongoTemplate.findById(importJobSpecification.getId(), JobSpecificationEntity.class);
        Assertions.assertNotNull(jobSpecificationEntity.getSchedule());
        List<JobEntity> scheduledTaskEntities = assertAndGetTerminationJobCreatedFromSpec(importJobSpecification.getId());
        Assertions.assertEquals(JobTypeEnum.IMPORTJOB, scheduledTaskEntities.get(0).getAtType());
        Assertions.assertEquals(JobSchedulerType.IMMEDIATEJOBSCHEDULER, jobSpecificationEntity.getSchedule().getAtType());
    }
    @Test
    void givenImportJobSpecificationWithNullSchedule_whenCreateTerminationJobSpecification_thenBadRequest() throws Exception {
        String jobSpecification = toJsonString(ImportJobSpecification.builder().atType(JobSpecificationType.IMPORTJOBSPECIFICATION.getValue()).importType(ImportTypeEnum.INSERT)
                .build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), JOB_SCHEDULE_MUST_NOT_BE_NULL, INVALID_INPUT.getStatus());
    }

    @Test
    void givenPurgeJobSpecificationWithNullSchedule_whenCreateTerminationJobSpecification_thenBadRequest() throws Exception {
        String jobSpecification = toJsonString(
                PurgeJobSpecification.builder()
                        .atType("PurgeJobSpecification")
                        .purgeType(PURGEPRODUCT)
                        .query("status=Created")
                        .build()
        );
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), JOB_SCHEDULE_MUST_NOT_BE_NULL, INVALID_INPUT.getStatus());
    }

    @Test
    void givenTerminationJobSpecificationWithNullSchedule_whenCreateTerminationJobSpecification_thenBadRequest() throws Exception {
        String jobSpecification = toJsonString(
                TerminationJobSpecification.builder()
                        .atType("TerminationJobSpecification")
                        .build()
        );
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), JOB_SCHEDULE_MUST_NOT_BE_NULL, INVALID_INPUT.getStatus());
    }

    @Test
    void givenExportJobJobTypeWithNullStartDate_whenCreate_thenBadRequest() throws Exception {

        String validTypeJob = toJsonString(
                ExportJobSpecification
                        .builder()
                        .atType("ExportJobSpecification")
                        .contentType(ContentTypeEnum.JSON)
                        .query(EXPORT_QUERY_BY_STATUS)
                        .schedule(RecurringJobScheduler
                                .builder()
                                .scheduledPeriod(DatePeriod.builder().build())
                                .frequency(JobFrequency.builder().amount(1).timePeriod(TimePeriodType.DAY).build())
                                .executionTime("20:00")
                                .build())
                        .build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validTypeJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(FIELD_MUST_NOT_BE_NULL, RecurringJobScheduler.Fields.scheduledPeriod + "." + DatePeriod.Fields.startDate), INVALID_INPUT.getStatus());
    }

    @Test
    void givenExportJobJobTypeWithHourFrequencyMoreThan24_whenCreate_thenBadRequest() throws Exception {

        String validTypeJob = toJsonString(
                ExportJobSpecification
                        .builder()
                        .atType("ExportJobSpecification")
                        .contentType(ContentTypeEnum.JSON)
                        .query(EXPORT_QUERY_BY_STATUS)
                        .schedule(RecurringJobScheduler
                                .builder()
                                .scheduledPeriod(DatePeriod.builder().startDate(LocalDate.now().plusDays(1)).build())
                                .frequency(JobFrequency.builder().amount(26).timePeriod(TimePeriodType.HOUR).build())
                                .executionTime("20:00")
                                .build())
                        .build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validTypeJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), FREQUENCY_AMOUNT_CANNOT_BE_MORE_THAN_24_FOR_PERIOD_TYPE_HOUR, INVALID_INPUT.getStatus());
    }

    @Test
    void givenExportJobJobTypeWithNullPlannedDateForOneTimeJobScheduler_whenCreate_thenBadRequest() throws Exception {

        String validTypeJob = toJsonString(
                ExportJobSpecification
                        .builder()
                        .atType("ExportJobSpecification")
                        .contentType(ContentTypeEnum.JSON)
                        .query(EXPORT_QUERY_BY_STATUS)
                        .schedule(OneTimeJobScheduler
                                .builder()
                                .plannedDate(null)
                                .build())
                        .build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(validTypeJob), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, MISSING_INPUT.getCode(), String.format(FIELD_MUST_NOT_BE_NULL, JobSpecification.Fields.schedule + "." + OneTimeJobScheduler.Fields.plannedDate), MISSING_INPUT.getStatus());
    }
    @ParameterizedTest
    @ValueSource(strings = {"Created,Aborted", "invalidValue"})
    void givenExportJobJobTypeWithInvalidEnumQueryFilterValue_whenCreate_thenBadRequest(String queryValue) throws Exception {

        String jobSpecification = toJsonString(ExportJobSpecification
                .builder()
                .atType("ExportJobSpecification")
                .contentType(ContentTypeEnum.JSON)
                .query("status=" + queryValue)
                .schedule(ImmediateJobScheduler.builder().build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(INVALID_FILTER_VALUE_FOR_ENUM, queryValue, ProductStatusType.class.getSimpleName()), INVALID_INPUT.getStatus());

    }

    @ParameterizedTest
    @ValueSource(strings = {"Created,Aborted", "invalidValue"})
    void givenPurgeProductJobTypeWithInvalidEnumQueryFilterValue_whenCreate_thenBadRequest(String queryValue) throws Exception {
        when(securityService.hasRoleEntitlement(Roles.PURGE_PRODUCT)).thenReturn(true);

        String jobSpecification = toJsonString(PurgeJobSpecification
                .builder()
                .atType("PurgeJobSpecification")
                .query("status=" + queryValue)
                .purgeType(PurgeTypeEnum.PURGEPRODUCT)
                .schedule(ImmediateJobScheduler.builder().build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(INVALID_FILTER_VALUE_FOR_ENUM, queryValue, ProductStatusType.class.getSimpleName()), INVALID_INPUT.getStatus());

    }

    @ParameterizedTest
    @ValueSource(strings = {"2022-01-01", "2022-01-01T25:00:00", "2022-01-0100:00:00Z", "anything"})
    void givenExportJobJobTypeWithInvalidDateQueryFilterValue_whenCreate_thenBadRequest(String invalidDateformat) throws Exception {
        String jobSpecification = toJsonString(ExportJobSpecification
                .builder()
                .atType("ExportJobSpecification")
                .contentType(ContentTypeEnum.JSON)
                .query("startDate.gte=" + invalidDateformat)
                .schedule(ImmediateJobScheduler.builder().build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), INVALID_DATE_FORMAT_EXPECTED_ISO_8601_FORMAT, INVALID_INPUT.getStatus());

    }

    @Test
    void givenExportJobJobTypeWithInvalidFieldAsDateQueryFilterValue_whenCreate_thenBadRequest() throws Exception {
        String jobSpecification = toJsonString(ExportJobSpecification
                .builder()
                .atType("ExportJobSpecification")
                .contentType(ContentTypeEnum.JSON)
                .query("status.gte=" + "2025-02-20T10:48:55Z")
                .schedule(ImmediateJobScheduler.builder().build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertBadRequestErrorExists(error, INVALID_INPUT.getCode(), String.format(FIELD_S_IS_NOT_A_VALID_DATE_OR_DATETIME_FIELD, "status"), INVALID_INPUT.getStatus());

    }
}
