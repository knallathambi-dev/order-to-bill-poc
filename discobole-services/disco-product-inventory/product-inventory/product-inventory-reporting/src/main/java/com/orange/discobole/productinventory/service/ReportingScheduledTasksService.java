// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.model.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.orange.discobole.productinventory.service.ReportService.getReportQuery;

@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@Service
@Slf4j
public class ReportingScheduledTasksService {

    private final ReportCronRunStateService reportCronRunStateService;
    private final MongoTemplate mongoTemplate;
    private final TransactionTemplate transactionTemplate;

    //@formatter:off
    private static Update createBaseDateUpdate(LocalDate date) {
        return new Update()
                .setOnInsert(BaseDateDerivedFields.Fields.date, date)
                .setOnInsert(BaseDateDerivedFields.Fields.dayOfMonth, date.getDayOfMonth())
                .setOnInsert(BaseDateDerivedFields.Fields.dayOfYear, date.getDayOfYear())
                .setOnInsert(BaseDateDerivedFields.Fields.isoDayOfWeek, date.getDayOfWeek().getValue());
    }


    private static Update createUpdate(StatusReportingFields statusReportEntity, LocalDate date) {
        Update update = createBaseDateUpdate(date);
        return update
                .inc(StatusReportingFields.Fields.abortedCount, Optional.ofNullable(statusReportEntity.getAbortedCount()).orElse(0L))
                .inc(StatusReportingFields.Fields.activeCount, Optional.ofNullable(statusReportEntity.getActiveCount()).orElse(0L))
                .inc(StatusReportingFields.Fields.terminatedCount, Optional.ofNullable(statusReportEntity.getTerminatedCount()).orElse(0L))
                .inc(StatusReportingFields.Fields.cancelledCount, Optional.ofNullable(statusReportEntity.getCancelledCount()).orElse(0L))
                .inc(StatusReportingFields.Fields.createdCount, Optional.ofNullable(statusReportEntity.getCreatedCount()).orElse(0L))
                .inc(StatusReportingFields.Fields.soldCount, Optional.ofNullable(statusReportEntity.getSoldCount()).orElse(0L));
    }

    private static Update createEmptyUpdate() {
        LocalDate now = LocalDate.now();
        Update update = createBaseDateUpdate(now);
        return update
                .inc(StatusReportingFields.Fields.abortedCount, 0L)
                .inc(StatusReportingFields.Fields.activeCount, 0L)
                .inc(StatusReportingFields.Fields.terminatedCount, 0L)
                .inc(StatusReportingFields.Fields.cancelledCount, 0L)
                .inc(StatusReportingFields.Fields.createdCount, 0L)
                .inc(StatusReportingFields.Fields.soldCount, 0L);
    }
    //@formatter:on

    @Scheduled(cron = "${scheduling.reporting.cronExpression}")
    @SchedulerLock(name = "${scheduling.reporting.lockName}", lockAtMostFor = "${scheduling.reporting.lockAtMostFor}", lockAtLeastFor = "${scheduling.reporting.lockAtLeastFor}")
    public void performDailyReport() {
        LocalDate today = LocalDate.now();
        /**
         * Using TransactionTemplate ensures that the operations within the transaction
         * (checking if the task has run, marking it as run, and importing data) are executed
         * atomically. This prevents race conditions and guarantees that importPreviousDayData
         * runs only once per day.
         */
        transactionTemplate.execute(status -> {
            if (!reportCronRunStateService.hasRunForDate(today)) {
                markForDateAndImport(today);
            }
            return null;
        });


    }

    public void markForDateAndImport(LocalDate date) {
        reportCronRunStateService.markAsRunForDate(date);
        importPreviousDayData();
    }

    public Optional<StatusReportEntity> findFirstStatusReportByOrderByDateDesc() {
        Query query = new Query()
                .with(Sort.by(Sort.Order.desc(BaseDateDerivedFields.Fields.date)))
                .limit(1);
        StatusReportEntity statusReportEntity = mongoTemplate.findOne(query, StatusReportEntity.class);
        return Optional.ofNullable(statusReportEntity);
    }

    private List<ProductOfferReportEntity> findLatestProductReportsReportForEachOffer() {

        List<String> productOfferIds = mongoTemplate.findDistinct(
                ProductOfferEntity.Fields.id, ProductOfferEntity.class, String.class);

        List<ProductOfferReportEntity> latestReports = new ArrayList<>();
        for (String productOfferId : productOfferIds) {
            Query query = new Query()
                    .addCriteria(Criteria
                            .where(ProductOfferReportEntity.Fields.productOfferId)
                            .is(productOfferId))
                    .with(Sort.by(Sort.Direction.DESC, BaseDateDerivedFields.Fields.date))
                    .limit(1);

            ProductOfferReportEntity latestReport = mongoTemplate.findOne(query, ProductOfferReportEntity.class);
            if (latestReport != null) {
                latestReports.add(latestReport);
            }
        }


        return latestReports;
    }


    private void importPreviousDayData() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        importProductOfferReportData(yesterday, today);
        importStatusReportData(yesterday, today);
    }

    private void importProductOfferReportData(LocalDate yesterday, LocalDate today) {
        List<ProductOfferReportEntity> productOfferReportEntities = findLatestProductReportsReportForEachOffer();
        for (ProductOfferReportEntity productOfferReportEntity : productOfferReportEntities) {
            if (yesterday.isAfter(productOfferReportEntity.getDate())) {
                LocalDate startDate = productOfferReportEntity.getDate().plusDays(1);
                log.info("Creating entries from '{}' to '{}'", startDate, today);
                List<LocalDate> missingDates = startDate.datesUntil(today.plusDays(1)).toList();
                missingDates.forEach(date -> {
                    Update update = createUpdate(productOfferReportEntity, date);
                    createAndSaveReport(
                            update,
                            getReportQuery(today, productOfferReportEntity.getProductOfferId()),
                            today,
                            ProductOfferReportEntity.class);
                });
            } else {
                Update update = createUpdate(productOfferReportEntity, today);
                createAndSaveReport(
                        update,
                        getReportQuery(today, productOfferReportEntity.getProductOfferId()),
                        today,
                        ProductOfferReportEntity.class);
            }
        }

    }


    private void importStatusReportData(LocalDate yesterday, LocalDate today) {
        Optional<StatusReportEntity> optionalStatusReport = findFirstStatusReportByOrderByDateDesc();

        if (optionalStatusReport.isPresent()) {
            StatusReportEntity statusReportEntity = optionalStatusReport.get();
            if (yesterday.isAfter(statusReportEntity.getDate())) {
                LocalDate startDate = statusReportEntity.getDate().plusDays(1);
                log.info("Creating entries from '{}' to '{}'", startDate, today);

                List<LocalDate> missingDates = startDate.datesUntil(today.plusDays(1)).toList();

                missingDates.forEach(date -> {
                    Update update = createUpdate(statusReportEntity, date);
                    createAndSaveReport(update, getReportQuery(date), date, StatusReportEntity.class);
                });
            } else {
                Update update = createUpdate(statusReportEntity, today);
                createAndSaveReport(update, getReportQuery(today), today, StatusReportEntity.class);
            }
        } else {
            createAndSaveReport(createEmptyUpdate(), getReportQuery(today), today, StatusReportEntity.class);
        }
    }


    public <T extends BaseDateDerivedFields> void createAndSaveReport(Update update, Query query, LocalDate date, Class<T> klass) {
        try {
            mongoTemplate.upsert(query, update, klass);
            log.info("Upsert operation completed for date '{}'", date);
        } catch (Exception ex) {
            log.error("Failed to upsert {} for date '{}': {}", klass.getName(), date, ex.getMessage());
            throw ex;
        }
    }
}
