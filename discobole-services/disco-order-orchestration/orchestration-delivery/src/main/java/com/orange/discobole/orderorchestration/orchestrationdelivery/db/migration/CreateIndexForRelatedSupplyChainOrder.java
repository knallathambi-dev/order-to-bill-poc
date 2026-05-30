// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.db.migration;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "createRelatedSupplyChainOrderIndexes", order = "005", author = "mongock", runAlways = true)
@RequiredArgsConstructor
public class CreateIndexForRelatedSupplyChainOrder {

    private final MongoDatabase mongoDatabase;

    @Execution
    public void createIndexes() {
        var collection = mongoDatabase.getCollection("orchestrationPlan");

        collection.createIndex(Indexes.ascending("orchestrationPlanNodes.relatedSupplyChainOrder._id"),
                new IndexOptions().unique(false));

        collection.createIndex(Indexes.ascending("orchestrationPlanNodes.relatedSupplyChainOrder.orderItemId"),
                new IndexOptions().unique(false));
    }
    @RollbackExecution
    public void rollback(MongoTemplate db) {
        var collection = mongoDatabase.getCollection("orchestrationPlan");

        // Drop using the actual index names
        collection.dropIndex("orchestrationPlanNodes.relatedSupplyChainOrder._id_1");
        collection.dropIndex("orchestrationPlanNodes.relatedSupplyChainOrder.orderItemId_1");
    }
}
