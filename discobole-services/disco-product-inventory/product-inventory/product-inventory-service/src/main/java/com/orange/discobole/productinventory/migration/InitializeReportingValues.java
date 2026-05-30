// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;

/**
 * @author Med khames Guen
 */

import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.model.BaseDateDerivedFields;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.StatusReportEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;

import static com.orange.discobole.productinventory.kafka.consumer.impl.reports.ReportingConsumer.STATUS_FIELD_MAP;
@ChangeUnit(id = "initialize-reporting-values", order = "008", author = "khames")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class InitializeReportingValues {

    public static final String STATUS = "status";
    public static final String COUNT = "count";
    private final MongoTemplate mongoTemplate;


    @Execution
    public void execute() {
        try {
            log.info("Starting aggregation to calculate product counts by status...");

            // Define aggregation to count products by status
            Aggregation aggregation = Aggregation.newAggregation(Aggregation.group(STATUS).count().as(COUNT), // Group by 'status' and count
                    Aggregation.project(COUNT).and("_id").as(STATUS) // Map '_id' to 'status'
            );

            // Execute aggregation using ProductEntity.class
            AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, ProductEntity.class, Document.class);
            LocalDate now = LocalDate.now();
            Update update = new Update();
            // Process results and save to StatusReportEntity
            results.getMappedResults().forEach(doc -> {
                ProductStatusType status = ProductStatusType.valueOf(doc.getString(STATUS));
                Number countNumber = doc.get(COUNT, Number.class); // Safely fetch as Number
                long count = countNumber != null ? countNumber.longValue() : 0; // Convert to long
                if (STATUS_FIELD_MAP.containsKey(status)) {
                    String field = STATUS_FIELD_MAP.get(status);
                    update.inc(field, count);
                    log.info("Incrementing '{}' by {} for date '{}'", field, count, now);
                }
            });
            update.setOnInsert(BaseDateDerivedFields.Fields.date, now);
            update.setOnInsert(BaseDateDerivedFields.Fields.dayOfMonth, now.getDayOfMonth());
            update.setOnInsert(BaseDateDerivedFields.Fields.dayOfYear, now.getDayOfYear());
            update.setOnInsert(BaseDateDerivedFields.Fields.isoDayOfWeek, now.getDayOfWeek().getValue());
            updateEntity(new Query(Criteria.where(BaseDateDerivedFields.Fields.date).is(now)), update, now);
            log.info("Aggregation completed successfully.");
        } catch (Exception e) {
            log.error("Failed to calculate and save product counts by status", e);
            throw e;
        }
    }

    private void updateEntity(Query query, Update update, LocalDate now) {
        try {
            mongoTemplate.upsert(query, update, StatusReportEntity.class);
            log.info("Upsert operation completed for date '{}'", now);
        } catch (Exception ex) {
            log.error("Failed to upsert status report for date '{}': {}", now, ex.getMessage());
            throw ex;
        }
    }


    @RollbackExecution
    public void rollback() {
        log.debug("rollback method");
    }
}