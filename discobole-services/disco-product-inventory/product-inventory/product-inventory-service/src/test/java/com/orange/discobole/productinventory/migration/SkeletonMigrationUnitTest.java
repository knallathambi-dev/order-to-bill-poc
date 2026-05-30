// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;

import com.orange.discobole.productinventory.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * @author Mostafa Saied
 */

@ExtendWith(MockitoExtension.class)
class SkeletonMigrationUnitTest {
    @Mock private ProductRepository productRepository;
    @InjectMocks private SkeletonMigration skeletonMigration;

    @Test
    void beforeExecution() {
        skeletonMigration.beforeExecution();

        Assertions.assertTrue(true);
    }

    @Test
    void rollBackBeforeExecution() {
        skeletonMigration.rollbackBeforeExecution();

        Assertions.assertTrue(true);
    }

    @Test
    void execution() {
        doReturn(1L).when(productRepository).count();

        skeletonMigration.execute();

        verify(productRepository, new Times(1)).count();
    }

    @Test
    void rollbackExecution() {
        skeletonMigration.rollback();

        Assertions.assertTrue(true);

    }
}
