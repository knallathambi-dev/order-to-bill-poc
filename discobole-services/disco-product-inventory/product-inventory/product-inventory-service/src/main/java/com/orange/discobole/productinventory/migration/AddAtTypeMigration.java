// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;

/**
 * @author Mostafa Saied
 */

import com.orange.discobole.productinventory.model.ProductEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PRODUCT;

@ChangeUnit(id = "add-at-type-migration", order = "002", author = "mostafa")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class AddAtTypeMigration {
    private static final String AT_TYPE = "atType";

    private final MongoTemplate mongoTemplate;
    @Execution
    public void execute() {
        UpdateDefinition updateDefinition = new Update().set(AT_TYPE, PRODUCT.getValue());

        mongoTemplate.updateMulti(new Query(), updateDefinition, ProductEntity.class);
    }

    @RollbackExecution
    public void rollback() {
        log.debug("rollback method");
    }
}