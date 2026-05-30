// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.projection;

import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleEntitySelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleEntitySelectEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * This class sends registered events on stream.
 *
 * @author Ankur Singh
 * @since 1.0
 */

class LifeCycleManagerProjectorTest extends ManageLifeCycleApplicationTests {
	
	private final LifeCycleManagerProjector lifeCycleManagerProjector ;
 
	private StreamBridge bridge;

	LifeCycleManagerProjectorTest() {
		lifeCycleManagerProjector = new LifeCycleManagerProjector();
		bridge = Mockito.mock(StreamBridge.class);
		ReflectionTestUtils.setField(lifeCycleManagerProjector, "bridge", bridge);
	}
	@Test
	void handle() {
		LifeCycleEntitySelectEvent lifeCycleEntitySelectEvent = new LifeCycleEntitySelectEvent("ert","abc", EntityType.ATOMICOFFER);
		lifeCycleManagerProjector.handle(lifeCycleEntitySelectEvent);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}
	
	@Test
	void handle1() {
		InvalidLifeCycleEntitySelectedEvent invalidLifeCycleEntitySelectedEventTest = new InvalidLifeCycleEntitySelectedEvent("def","ijk", EntityType.ATOMICOFFER);
		lifeCycleManagerProjector.handle(invalidLifeCycleEntitySelectedEventTest);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
		
	}
	
	@Test
	void handle2() {
			
		
		InvalidLifeCycleStateSelectedEvent invalidLifeCycleStateSelectedEvent = new InvalidLifeCycleStateSelectedEvent("lmn","opq", EntityType.PRODUCTSPECIFICATION,"ACTIVE",Set.of("RETIRED","INTEST"));
		lifeCycleManagerProjector.handle(invalidLifeCycleStateSelectedEvent);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
		
	}
	
	@Test
	void handle3() {
		LifeCycleStateSelectedEvent lifeCycleStateSelectedEvent = new LifeCycleStateSelectedEvent("lmn","opq", EntityType.PRODUCTSPECIFICATION,"ACTIVE","LAUNCHED",OffsetDateTime.now(),"0.1");
		lifeCycleManagerProjector.handle(lifeCycleStateSelectedEvent);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
		
	}

}
