// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;


import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static com.orange.discobole.productinventory.constant.Constant.*;


@ChangeUnit(id = "set-product-value-on-productRelationship-as-objectId", order = "004", author = "Molka")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class UpdateProductRelationship {

    private final MongoTemplate mongoTemplate;

    private static void calculateElapsed(long startTime) {
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        // Convert to milliseconds or seconds if needed
        double elapsedTimeInMillis = elapsedTime / 1_000_000.0;
        double elapsedTimeInSeconds = elapsedTime / 1_000_000_000.0;

        // Output the elapsed time
        log.info("Elapsed time");
        log.info("{} nanoseconds", elapsedTime);
        log.info("{} milliseconds", elapsedTimeInMillis);
        log.info("{} seconds", elapsedTimeInSeconds);
    }

    @Execution
    public void execute() {
        Query query = new Query(Criteria.where("productRelationship.product").exists(true));
        AtomicLong batchesProcessed = new AtomicLong();
        query.fields().include(PRODUCT_RELATIONSHIP).include(_ID);
        long startTime = System.nanoTime();
        try (Stream<Document> documentStream = mongoTemplate.stream(query, Document.class, COLLECTION_NAME)) {
            List<WriteModel<Document>> bulkOps = new ArrayList<>();
            documentStream.forEach(document -> {
                processDocument(document, bulkOps);
                if (bulkOps.size() >= BATCH_SIZE) {
                    executeBulkOperations(bulkOps, batchesProcessed);
                }
            });

            if (!bulkOps.isEmpty()) {
                executeBulkOperations(bulkOps, batchesProcessed);
            }

        }
        calculateElapsed(startTime);
    }

    private void processDocument(Document document, List<WriteModel<Document>> bulkOps) {
        List<Document> productRelationships = (List<Document>) document.get(PRODUCT_RELATIONSHIP);
        if (productRelationships != null) {
            List<Document> updatedRelationships = new ArrayList<>();
            boolean needsUpdate = false;

            for (Document relationship : productRelationships) {
                if (relationship.get(PRODUCT) != null && relationship.get(PRODUCT) instanceof com.mongodb.DBRef dbRef) {
                    ObjectId productId = null;
                    if (dbRef.getId() instanceof ObjectId) {
                        productId = (ObjectId) dbRef.getId();
                    }
                    Document updatedProduct = new Document(_ID, productId);
                    relationship.put(PRODUCT, updatedProduct);
                    needsUpdate = true;
                    updatedRelationships.add(relationship);
                }
            }

            if (needsUpdate) {
                Document updateDoc = new Document("$set", new Document(PRODUCT_RELATIONSHIP, updatedRelationships));
                bulkOps.add(new UpdateOneModel<>(new Document(_ID, document.get(_ID)), updateDoc));
            }
        }
    }

    private void executeBulkOperations(List<WriteModel<Document>> bulkOps, AtomicLong batchesProcessed) {
        mongoTemplate.getCollection(COLLECTION_NAME).bulkWrite(bulkOps);
        bulkOps.clear();
        log.info("Processed batch number {}", batchesProcessed.incrementAndGet());
    }

    @RollbackExecution
    public void rollback() {
        log.debug("rollback method");
    }
}