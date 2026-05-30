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
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.ContractLeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.LeadTimeHistorySampledStatistics;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "createContractLeadTimeIndexes", order = "009", author = "mongock", runAlways = true)
@RequiredArgsConstructor
public class CreateContractLeadTimeIndexesChangeUnit {

    private static final String COLLECTION = "contractLeadTimeHistorySampledStatistics";

    private final MongoDatabase mongoDatabase;


    @Execution
    public void createIndexes() {
        // Unique: contractName + sampleWindow
        mongoDatabase.getCollection(COLLECTION)
                .createIndex(
                        Indexes.ascending(
                                ContractLeadTimeHistorySampledStatistics.Fields.contractName,
                                LeadTimeHistorySampledStatistics.Fields.sampleWindow
                        ),
                        new IndexOptions().name("uxContractWindow").unique(true)
                );
    }

    @RollbackExecution
    public void rollback(MongoTemplate db) {
        mongoDatabase.getCollection(COLLECTION).dropIndex("uxContractWindow");
    }
}
