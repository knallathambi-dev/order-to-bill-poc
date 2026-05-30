// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.command.lifecycle;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;

 class SelectProductEntityCommandTest extends ManageLifeCycleApplicationTests {
@Test
void testSelectProductEntityCommandCreation() {
	SelectProductEntityCommand command=new SelectProductEntityCommand("1","2",EntityType.ATOMICOFFER.getValue());
	assertNotNull(command.toString());
	assertNotNull(command.getAggregateId());
	assertNotNull(command.getEntityType());
}
}
