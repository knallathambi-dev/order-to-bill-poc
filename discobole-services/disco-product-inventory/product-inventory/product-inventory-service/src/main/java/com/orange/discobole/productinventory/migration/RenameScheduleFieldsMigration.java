// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.disco.productinventory.migration;

import com.mongodb.client.result.UpdateResult;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@ChangeUnit(id = "rename-schedule-fields", order = "011", author = "khames")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class RenameScheduleFieldsMigration {

    private final MongoTemplate mongoTemplate;

    @Execution
    public void execute() {
        log.info("Starting migration: renaming endDateTime ➝ endDate and startDateTime ➝ startDate");

        Query query = getQueryForOldFields();
        List<Document> documents = mongoTemplate.find(query, Document.class, "jobSpecification");

        for (Document doc : documents) {
            processDocument(doc);
        }

        log.info("Migration completed.");
    }

    private Query getQueryForOldFields() {
        return new Query(new Criteria().orOperator(
                Criteria.where("schedule.scheduledPeriod.endDateTime").exists(true),
                Criteria.where("schedule.scheduledPeriod.startDateTime").exists(true)
        ));
    }

    private void processDocument(Document doc) {
        Document schedule = (Document) doc.get("schedule");
        if (schedule == null) {
            return;
        }

        Document scheduledPeriod = (Document) schedule.get("scheduledPeriod");
        if (scheduledPeriod == null) {
            return;
        }

        Update update = new Update();
        boolean hasUpdate = populateUpdate(scheduledPeriod, update);

        if (hasUpdate) {
            applyUpdate(doc, update);
        }
    }

    private boolean populateUpdate(Document scheduledPeriod, Update update) {
        boolean hasUpdate = false;

        if (scheduledPeriod.containsKey("endDateTime")) {
            update.set("schedule.scheduledPeriod.endDate", scheduledPeriod.get("endDateTime"));
            update.unset("schedule.scheduledPeriod.endDateTime");
            hasUpdate = true;
        }

        if (scheduledPeriod.containsKey("startDateTime")) {
            update.set("schedule.scheduledPeriod.startDate", scheduledPeriod.get("startDateTime"));
            update.unset("schedule.scheduledPeriod.startDateTime");
            hasUpdate = true;
        }

        return hasUpdate;
    }

    private void applyUpdate(Document doc, Update update) {
        Query idQuery = new Query(Criteria.where("_id").is(doc.get("_id")));
        UpdateResult result = mongoTemplate.updateFirst(idQuery, update, "jobSpecification");
        log.debug("Updated document with _id={} | matched={}, modified={}",
                doc.get("_id"), result.getMatchedCount(), result.getModifiedCount());
    }

    @RollbackExecution
    public void rollback() {
        log.debug("rollback method - no rollback implemented for rename");
    }
}
