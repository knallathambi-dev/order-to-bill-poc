// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleEntitySelectEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;

@ExtendWith(MockitoExtension.class)
class ManageLifeCycleEventHandlerServiceTest {

	@InjectMocks
	ManageLifeCycleEventHandlerService manageLifeCycleEventHandlerService;


	@Mock
	Publisher publisher;
 
	

	@Test
	void handleTest() {
		LifeCycleEntitySelectEvent event = new LifeCycleEntitySelectEvent("abc", "2",EntityType.PRODUCTSPECIFICATION);

		Mockito.doNothing().when(publisher).project(List.of(event));
		manageLifeCycleEventHandlerService.handle(event);
		assertEquals("abc", event.getAggregateId());
	}
	
	@Test
	void handleTest2() {
		LifeCycleStateSelectedEvent event = new LifeCycleStateSelectedEvent("abc", "3",EntityType.PRODUCTSPECIFICATION,"LAUNCHED","ACTIVE",OffsetDateTime.now(),"0.1");

		Mockito.doNothing().when(publisher).project(List.of(event));
		manageLifeCycleEventHandlerService.handle(event);
		assertEquals("abc", event.getAggregateId());
	}
}

	