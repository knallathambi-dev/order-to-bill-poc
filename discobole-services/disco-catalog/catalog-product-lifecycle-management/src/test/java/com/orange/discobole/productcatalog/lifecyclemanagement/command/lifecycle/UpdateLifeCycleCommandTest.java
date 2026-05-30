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
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.LifecycleState;

 class UpdateLifeCycleCommandTest extends ManageLifeCycleApplicationTests {
@Test
void testUpdateLifeCycleCommandCreation() {
	UpdateLifeCycleCommand command=new UpdateLifeCycleCommand("1",LifecycleState.ACTIVE,"0.1");
	assertNotNull(command.toString());
	assertNotNull(command.getAggregateId());
	assertNotNull(command.getState());
	
}
}
