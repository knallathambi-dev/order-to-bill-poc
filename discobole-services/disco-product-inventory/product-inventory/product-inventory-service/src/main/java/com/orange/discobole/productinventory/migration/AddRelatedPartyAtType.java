// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;

import com.orange.discobole.productinventory.model.ProductEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ChangeUnit(id = "add-related-party-at-type", order = "007", author = "khames")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class AddRelatedPartyAtType {

    private static final String RELATED_PARTY = "relatedParty";
    private static final String RELATED_PARTY_AT_TYPE = "relatedParty.$[].atType";

    private final MongoTemplate mongoTemplate;

    @Execution
    public void execute() {
        // Query to find products where relatedParty exists and is not empty
        Query query = new Query();
        query.addCriteria(Criteria.where(RELATED_PARTY).exists(true).ne(null).not().size(0));

        // Update to set atType for all items in the relatedParty array
        Update update = new Update().set(RELATED_PARTY_AT_TYPE, "PartyRef");

        // Apply the update
        mongoTemplate.updateMulti(query, update, ProductEntity.class);

        log.info("Successfully added 'atType' to relatedParty items in eligible ProductEntity documents.");
    }

    @RollbackExecution
    public void rollback() {
        // Rolling back the change by removing the atType field from relatedParty array items
        Query query = new Query();
        query.addCriteria(Criteria.where(RELATED_PARTY_AT_TYPE).exists(true));

        Update update = new Update().unset(RELATED_PARTY_AT_TYPE);

        mongoTemplate.updateMulti(query, update, ProductEntity.class);

        log.info("Rollback executed: Removed 'atType' from relatedParty items.");
    }
}
