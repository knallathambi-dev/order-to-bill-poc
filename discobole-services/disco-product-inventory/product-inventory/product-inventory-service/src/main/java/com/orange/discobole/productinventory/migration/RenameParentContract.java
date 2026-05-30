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


@ChangeUnit(id = "rename-parentContract-to-rootProduct", order = "004", author = "Khaoula")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class RenameParentContract {

    private static final String RELATIONSHIP_TYPE = "productRelationship.relationshipType";

    private final MongoTemplate mongoTemplate;
    @Execution
    public void execute() {
        Query query = new Query();
        query.addCriteria(
                Criteria.where(RELATIONSHIP_TYPE).is("PARENTCONTRACT")
        );

        Update update = new Update().set("productRelationship.$[elem].relationshipType", "ROOTPRODUCT");
        update.filterArray(Criteria.where("elem.relationshipType").is("PARENTCONTRACT"));
        mongoTemplate.updateMulti(query, update, ProductEntity.class);
    }

    @RollbackExecution
    public void rollback() {
        log.debug("rollback method");
    }
}
