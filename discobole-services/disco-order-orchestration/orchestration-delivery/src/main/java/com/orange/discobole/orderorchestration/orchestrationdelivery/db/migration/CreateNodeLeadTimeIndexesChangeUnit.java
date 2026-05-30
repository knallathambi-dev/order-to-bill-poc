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
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.LeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.NodeLeadTimeHistorySampledStatistics;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "createNodeLeadTimeIndexes", order = "008", author = "mongock", runAlways = true)
@RequiredArgsConstructor
public class CreateNodeLeadTimeIndexesChangeUnit {
    private static final String COLLECTION = "nodeLeadTimeHistorySampledStatistics";

    private final MongoDatabase mongoDatabase;

    @Execution
    public void createIndexes() {
        // Create the new unique index: productSpecId + sampleWindow
        mongoDatabase.getCollection(COLLECTION)
                .createIndex(
                        Indexes.ascending(
                                NodeLeadTimeHistorySampledStatistics.Fields.productSpecId,
                                LeadTimeHistorySampledStatistics.Fields.sampleWindow
                        ),
                        new IndexOptions().name("uxNodeSpecWindow").unique(true)
                );

        // Create the NON-unique index: deliveryFactoryName + sampleWindow
        mongoDatabase.getCollection(COLLECTION)
                .createIndex(
                        Indexes.ascending(
                                NodeLeadTimeHistorySampledStatistics.Fields.deliveryFactoryName,
                                LeadTimeHistorySampledStatistics.Fields.sampleWindow
                        ),
                        new IndexOptions().name("ixNodeFactoryWindow").unique(false)
                );
    }

    @RollbackExecution
    public void rollback(MongoTemplate db) {
        mongoDatabase.getCollection(COLLECTION).dropIndex("uxNodeSpecWindow");
        mongoDatabase.getCollection(COLLECTION).dropIndex("ixNodeFactoryWindow");
    }
}
