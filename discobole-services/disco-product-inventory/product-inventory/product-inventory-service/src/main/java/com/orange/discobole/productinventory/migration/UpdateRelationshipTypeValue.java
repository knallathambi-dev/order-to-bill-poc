// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;

import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
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

@ChangeUnit(id = "update-relationship-type-value", order = "004", author = "khames")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class UpdateRelationshipTypeValue {

    private static final String PRODUCT_RELATIONSHIP_RELATIONSHIP_TYPE = "productRelationship.relationshipType";
    private static final String PRODUCT_RELATIONSHIP_RELATIONSHIP_TYPE_UPDATE = "productRelationship.$[selector].relationshipType";

    private final MongoTemplate mongoTemplate;

    @Execution
    public void execute() {
        for (ProductRelationshipType value : ProductRelationshipType.values()) {
            Query query = new Query();
            query.addCriteria(
                    Criteria.where(PRODUCT_RELATIONSHIP_RELATIONSHIP_TYPE).is(value)
            );
            Update update = new Update().set(PRODUCT_RELATIONSHIP_RELATIONSHIP_TYPE_UPDATE, value.getValue())
                    .filterArray(Criteria.where("selector.relationshipType").is(value));
            mongoTemplate.updateMulti(query, update, ProductEntity.class);
        }
    }

    @RollbackExecution
    public void rollback() {
        log.debug("rollback method");
    }
}
