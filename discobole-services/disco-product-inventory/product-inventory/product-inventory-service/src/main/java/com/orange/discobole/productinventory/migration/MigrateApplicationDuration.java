// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static com.orange.discobole.productinventory.constant.Constant.COLLECTION_NAME;

@ChangeUnit(id = "migrate-application-duration", order = "012", author = "khames")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class MigrateApplicationDuration {

    private static final int BATCH_SIZE = 100;
    private static final String UNITS = "units";
    private static final String PRODUCT_PRICE = "productPrice";

    private final MongoTemplate mongoTemplate;

    @Execution
    public void execute() {
        processApplicationDurations(
                "productPrice.applicationDuration",
                (product, updatesForProduct) -> {
                    List<Document> prices = (List<Document>) product.get(PRODUCT_PRICE);
                    for (int i = 0; i < prices.size(); i++) {
                        String updatePath = "productPrice." + i + ".applicationDuration";
                        Document price = prices.get(i);
                        appendWriteModel(price, updatePath, product, updatesForProduct);
                    }
                }
        );

        processApplicationDurations(
                "productPrice.productPriceAlteration.applicationDuration",
                (product, updatesForProduct) -> {
                    List<Document> prices = (List<Document>) product.get(PRODUCT_PRICE);
                    for (int i = 0; i < prices.size(); i++) {
                        List<Document> alterations = (List<Document>) prices.get(i).get("productPriceAlteration");
                        for (int j = 0; j < alterations.size(); j++) {
                            String updatePath = "productPrice." + i + ".productPriceAlteration." + j + ".applicationDuration";
                            Document alteration = alterations.get(j);
                            appendWriteModel(alteration, updatePath, product, updatesForProduct);
                        }
                    }
                }
        );
    }

    private void appendWriteModel(Document document, String updatePath, Document product, List<WriteModel<Document>> updatesForProduct) {
        Object duration = document.get("applicationDuration");
        Document recurringPeriod = (Document) document.get("recurringChargePeriod");
        Document newDuration = transformDuration(duration, recurringPeriod);
        if (newDuration != null && !newDuration.isEmpty()) {
            updatesForProduct.add(new UpdateOneModel<>(
                    Filters.eq("_id", product.getObjectId("_id")),
                    new Document("$set", new Document(updatePath, newDuration))
            ));
        }

    }

    private void processApplicationDurations(String queryPath, BiConsumer<Document, List<WriteModel<Document>>> updater) {
        List<WriteModel<Document>> batch = new ArrayList<>();

        try (Stream<Document> products = mongoTemplate.stream(
                Query.query(Criteria.where(queryPath).ne(null)),
                Document.class,
                COLLECTION_NAME
        )) {
            products.forEach(product -> {
                Document productDoc = (Document) mongoTemplate.getConverter().convertToMongoType(product);
                List<WriteModel<Document>> updatesForProduct = new ArrayList<>();
                updater.accept(productDoc, updatesForProduct);
                if (!updatesForProduct.isEmpty()) {
                    batch.addAll(updatesForProduct);
                }
                if (batch.size() >= BATCH_SIZE) {
                    flushBatch(batch);
                }
            });
        }

        if (!batch.isEmpty()) {
            flushBatch(batch);
        }
    }

    private Document transformDuration(Object duration, Document recurringPeriod) {
        if (duration == null) {
            return new Document();
        }

        Float amount = null;
        if (duration instanceof Integer i) {
            amount = i.floatValue();
        } else if (duration instanceof Double d) {
            amount = d.floatValue();
        } else if (duration instanceof Float f) {
            amount = f;
        }

        if (amount != null) {
            String unit = (recurringPeriod != null && recurringPeriod.get(UNITS) != null)
                    ? recurringPeriod.getString(UNITS)
                    : null;
            return new Document("amount", amount).append(UNITS, unit);
        }
        return new Document();
    }

    private void flushBatch(List<WriteModel<Document>> batch) {
        try {
            mongoTemplate.getCollection(COLLECTION_NAME).bulkWrite(batch);
            log.info("Flushed batch of size {}", batch.size());
        } catch (Exception e) {
            log.error("Bulk update failed", e);
        } finally {
            batch.clear();
        }
    }

    @RollbackExecution
    public void rollback() {
        log.debug("rollback method not implemented");
    }
}