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

import com.orange.discobole.productinventory.repository.ProductRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.mongock.api.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ChangeUnit(id = "skeleton-migration", order = "001", author = "mostafa")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class SkeletonMigration {
    private final ProductRepository productRepository;

    @BeforeExecution
    public void beforeExecution() {
        log.debug("beforeExecution method");
    }

    @RollbackBeforeExecution
    public void rollbackBeforeExecution() {
        log.debug("beforeExecution method");
    }

    @Execution
    public void execute() {
        long count = productRepository.count();
        log.debug("Products count: {}", count);
    }

    /**
     This method is mandatory even when transactions are enabled.
     They are used in the undo operation and any other scenario where transactions are not an option.
     However, note that when transactions are avialble and Mongock need to rollback, this method is ignored.
     **/
    @RollbackExecution
    public void rollback() {
        log.debug("rollback method");
    }
}