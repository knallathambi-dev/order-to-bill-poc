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
import com.orange.discobole.productinventory.constant.EntityFields;
import com.orange.discobole.productinventory.model.ProductEntity;
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
import java.util.function.Consumer;
import java.util.stream.Stream;

@ChangeUnit(id = "product-alteration-with-is-root-product-flag", order = "011", author = "abdelgawad")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class ProductAlterationWithIsRootProductFlag {
    private static final int BATCH_SIZE = 10000;

    private final MongoTemplate mongoTemplate;

    @Execution
    public void execute() {
        final Query query = Query.query(
                Criteria
                        .where(EntityFields.Product.IS_ROOT_PRODUCT).exists(false)
                        .and(EntityFields.Product.PRODUCT_RELATIONSHIP_DOT_RELATIONSHIP_TYPE).ne("rootProduct")
        );
        query.fields().include(EntityFields.Product._ID);

        try (Stream<ProductEntity> stream = this.mongoTemplate.stream(query, ProductEntity.class)) {
            final Consumer<Stream<ProductEntity>> bulkOps = createIsRootProductBulkOps(BATCH_SIZE);
            bulkOps.accept(stream);
        }
    }

    private Consumer<Stream<ProductEntity>> createIsRootProductBulkOps(final int batchSize) {
        List<WriteModel<Document>> bulkOps = new ArrayList<>();

        Runnable executeBatch = () -> {
            if (!bulkOps.isEmpty()) {
                this.mongoTemplate.getCollection(EntityFields.Product.COLLECTION_NAME).bulkWrite(bulkOps);
                bulkOps.clear();
            }
        };

        return productsStream -> {
            productsStream.forEach(product -> {
                Document updateDoc = new Document("$set", new Document(EntityFields.Product.IS_ROOT_PRODUCT, Boolean.TRUE));
                UpdateOneModel<Document> objectUpdateOneModel = new UpdateOneModel<>(new Document(EntityFields.Product._ID, new ObjectId(product.getId())), updateDoc);
                bulkOps.add(objectUpdateOneModel);
                if (bulkOps.size() >= batchSize) {
                    executeBatch.run();
                }
            });
            executeBatch.run();
        };
    }

    @RollbackExecution
    public void rollback() {
        log.debug("rollback method");
    }
}
